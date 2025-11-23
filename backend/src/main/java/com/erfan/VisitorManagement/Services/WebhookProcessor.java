package com.erfan.VisitorManagement.Services;

import com.erfan.VisitorManagement.Dtos.CreateVisitDto;
import com.erfan.VisitorManagement.Dtos.WhatsAppWebhookDto;
import com.erfan.VisitorManagement.Config.WhatsAppProperties;
import com.erfan.VisitorManagement.Models.Tenant;
import com.erfan.VisitorManagement.Models.Visit;
import com.erfan.VisitorManagement.Repos.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookProcessor {
    private final VisitService visitService;
    private final TenantRepository tenantRepository;
    private final WhatsAppService whatsAppService;
    private final WhatsAppProperties props;

    public void process(WhatsAppWebhookDto dto) {
        if (dto.getInteractiveReplyId() != null) {
            handleInteractive(dto);
        } else {
            handleText(dto);
        }
    }

    private void handleInteractive(WhatsAppWebhookDto dto) {
        String id = dto.getInteractiveReplyId();
        try {
            if (id.startsWith("APP_")) {
                Long visitId = Long.parseLong(id.substring(4));
                Visit v = visitService.approveVisit(visitId);
                String base = props.getPublicBaseUrl() != null ? props.getPublicBaseUrl() : "";
                String publicQrUrl = base + "/qrcodes/visit_"+visitId+".png";
                whatsAppService.sendImage(v.getVisitorPhone(), publicQrUrl);
            } else if (id.startsWith("REJ_")) {
                Long visitId = Long.parseLong(id.substring(4));
                visitService.rejectVisit(visitId);
            }
        } catch (Exception e) {
            log.error("Failed to process interactive reply {}", id, e);
        }
    }

    private void handleText(WhatsAppWebhookDto dto) {
        // naive parsing: expect lines - name, phone, datetime ISO, tenant phone
        String[] lines = dto.getMessageBody().split("\n");
        String name = lines.length>0?lines[0].trim():"";
        String phone = lines.length>1?lines[1].trim():dto.getFromPhone();
        String dtStr = lines.length>2?lines[2].trim():null;
        String tenantPhone = lines.length>3?lines[3].trim():null;
        if (tenantPhone == null) {
            whatsAppService.sendText(dto.getFromPhone(), "Please send tenant phone on 4th line");
            return;
        }
        Optional<Tenant> tOpt = tenantRepository.findByPhoneNumber(tenantPhone);
        if (tOpt.isEmpty()) {
            whatsAppService.sendText(dto.getFromPhone(), "Tenant not found");
            return;
        }
        LocalDateTime visitAt = dtStr!=null? LocalDateTime.parse(dtStr) : LocalDateTime.now();
        CreateVisitDto c = new CreateVisitDto();
        c.setVisitorName(name);
        c.setVisitorPhone(phone);
        c.setVisitDateTime(visitAt);
        c.setExpiryTime(visitAt.plusHours(4));
        c.setTenantId(tOpt.get().getId());
        var resp = visitService.createVisit(c);
        String approveId = "APP_"+resp.getId();
        String rejectId = "REJ_"+resp.getId();
        whatsAppService.sendTemplateWithButtons(tOpt.get().getPhoneNumber(),
                "Approve visitor "+name+"?", new String[]{approveId, rejectId});
    }
}

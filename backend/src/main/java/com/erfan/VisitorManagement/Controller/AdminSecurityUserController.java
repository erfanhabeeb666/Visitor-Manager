package com.erfan.VisitorManagement.Controller;

import com.erfan.VisitorManagement.Dtos.CreateSecurityUserDto;
import com.erfan.VisitorManagement.Dtos.SecurityUserDto;
import com.erfan.VisitorManagement.Enums.Status;
import com.erfan.VisitorManagement.Enums.UserType;
import com.erfan.VisitorManagement.Models.Security;
import com.erfan.VisitorManagement.Repos.SecurityRepository;
import com.erfan.VisitorManagement.Repos.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/security-users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminSecurityUserController {

    private final SecurityRepository securityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<SecurityUserDto> create(@Valid @RequestBody CreateSecurityUserDto dto) {
        if (userRepository.existsByEmail(dto.email.trim().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Security s = new Security();
        s.setName(dto.name);
        s.setEmail(dto.email.trim().toLowerCase());
        s.setPassword(passwordEncoder.encode(dto.password));
        s.setAdharUid(dto.adharUid);
        s.setUserType(UserType.SECURITY);
        s.setStatus(Status.ACTIVE);
        Security saved = securityRepository.save(s);
        return ResponseEntity.status(HttpStatus.CREATED).body(SecurityUserDto.fromEntity(saved));
    }

    @GetMapping
    public ResponseEntity<List<SecurityUserDto>> list() {
        List<SecurityUserDto> response = securityRepository.findAll()
                .stream()
                .map(SecurityUserDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SecurityUserDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateSecurityUserDto dto // reuse constraints; all fields required for simplicity
    ) {
        Security existing = securityRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String newEmail = dto.email.trim().toLowerCase();
        if (!existing.getEmail().equalsIgnoreCase(newEmail) && userRepository.findByEmail(newEmail).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        existing.setName(dto.name);
        existing.setEmail(newEmail);
        if (dto.password != null && !dto.password.isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.password));
        }
        existing.setAdharUid(dto.adharUid);
        Security saved = securityRepository.save(existing);
        return ResponseEntity.ok(SecurityUserDto.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Security existing = securityRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        existing.setStatus(Status.INACTIVE);
        securityRepository.save(existing);
        return ResponseEntity.noContent().build();
    }
}

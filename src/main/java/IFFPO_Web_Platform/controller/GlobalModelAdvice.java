package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final AdminNotificationService adminNotificationService;

    @ModelAttribute("notificationsCount")
    public long notificationsCount() {
        return adminNotificationService.countNouvelles();
    }
}

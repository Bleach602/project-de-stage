package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.dto.AdminNotificationDTO;
import IFFPO_Web_Platform.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/dashboard/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @GetMapping
    @ResponseBody
    public List<AdminNotificationDTO> liste() {
        return adminNotificationService.getNotifications();
    }
}
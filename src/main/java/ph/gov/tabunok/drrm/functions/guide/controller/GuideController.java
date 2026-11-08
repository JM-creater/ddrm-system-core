package ph.gov.tabunok.drrm.functions.guide.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.guide.dto.GuideDto;
import ph.gov.tabunok.drrm.functions.guide.service.GuideService;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @GetMapping
    public List<GuideDto> list() {
        return guideService.list();
    }
}
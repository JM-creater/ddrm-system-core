package ph.gov.tabunok.drrm.functions.directory.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ph.gov.tabunok.drrm.functions.directory.dto.EvacuationCenterDto;
import ph.gov.tabunok.drrm.functions.directory.dto.HotlineDto;
import ph.gov.tabunok.drrm.functions.directory.service.DirectoryService;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    @GetMapping("/centers")
    public List<EvacuationCenterDto> centers() {
        return directoryService.centers();
    }

    @GetMapping("/hotlines")
    public List<HotlineDto> hotlines() {
        return directoryService.hotlines();
    }
}
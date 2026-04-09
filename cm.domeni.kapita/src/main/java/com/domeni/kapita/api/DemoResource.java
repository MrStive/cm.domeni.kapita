package com.domeni.kapita.api;

import cm.domeni.generated.domeni.kapita.api.DemoApi;
import cm.domeni.generated.domeni.kapita.dto.CreateDemoDTO;
import cm.domeni.generated.domeni.kapita.dto.CreationResponseDTO;
import cm.domeni.generated.domeni.kapita.dto.DemoDTO;
import com.domeni.kapita.service.DemoService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoResource implements DemoApi {
  private final DemoService demoService;

  @Override
  public ResponseEntity<CreationResponseDTO> createDemo(CreateDemoDTO createDemoDTO) {
    UUID createdDemoId = demoService.createDemo(createDemoDTO);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreationResponseDTO().newId(createdDemoId));
  }

  @Override
  public ResponseEntity<List<DemoDTO>> fetchAllDemo() {
    return ResponseEntity.ok(demoService.fetchAllDemos());
  }

  @Override
  public ResponseEntity<DemoDTO> fetchDemoById(UUID demoId) {
    return ResponseEntity.ok(demoService.getByDemoId(demoId));
  }
}

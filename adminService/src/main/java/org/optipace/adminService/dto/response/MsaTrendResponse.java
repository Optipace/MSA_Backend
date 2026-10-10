package org.optipace.adminService.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsaTrendResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MsaTrendPointResponse> trend;
}


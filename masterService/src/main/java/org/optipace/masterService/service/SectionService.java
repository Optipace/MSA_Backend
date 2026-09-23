package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.OrganizationRequest;
import org.optipace.masterService.dto.request.OrganizationUpdateRequest;
import org.optipace.masterService.dto.request.SectionRequest;
import org.optipace.masterService.dto.request.SectionUpdateRequest;
import org.optipace.masterService.dto.response.*;
import org.springframework.data.domain.Pageable;

public interface SectionService {

    SingleResponse<PageResponse<ListOfSectionResponse>> getAllSections(Pageable pageable);

    SingleResponse<?> createSection(SectionRequest request, String adminId);

    SingleResponse<?> updateSection(Long sectionId, SectionUpdateRequest request, String adminId);

    SingleResponse<SectionResponse> getSectionById(Long sectionId);

    SingleResponse<?> deleteSectionById(Long sectionId, String adminId);
}

package gov.ca.cwds.cans.util;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.dto.logging.CreationLoggable;
import gov.ca.cwds.cans.domain.dto.logging.SubmitLoggable;
import gov.ca.cwds.cans.domain.dto.logging.UpdateLoggable;

/** @author denys.davydov */
public final class DtoCleaner {

  private DtoCleaner() {}

  public static Dto cleanDtoIfNeed(Dto inputDto) {
    if (inputDto == null) {
      return null;
    }

    if (inputDto instanceof CreationLoggable) {
      final CreationLoggable creationLoggable = (CreationLoggable) inputDto;
      creationLoggable.setCreatedBy(null);
      creationLoggable.setCreatedTimestamp(null);
    }

    if (inputDto instanceof UpdateLoggable) {
      final UpdateLoggable updateLoggable = (UpdateLoggable) inputDto;
      updateLoggable.setUpdatedBy(null);
      updateLoggable.setUpdatedTimestamp(null);
    }

    if (inputDto instanceof SubmitLoggable) {
      final SubmitLoggable submitLoggable = (SubmitLoggable) inputDto;
      submitLoggable.setSubmittedBy(null);
      submitLoggable.setSubmittedTimestamp(null);
    }

    return inputDto;
  }
}

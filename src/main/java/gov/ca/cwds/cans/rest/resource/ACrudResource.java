package gov.ca.cwds.cans.rest.resource;

import static gov.ca.cwds.cans.util.DtoCleaner.cleanDtoIfNeed;

import gov.ca.cwds.cans.domain.dto.Dto;
import gov.ca.cwds.cans.domain.entity.Persistent;
import gov.ca.cwds.cans.domain.mapper.AMapper;
import gov.ca.cwds.cans.rest.ResponseUtil;
import gov.ca.cwds.cans.service.AbstractCrudService;
import java.io.Serializable;
import javax.ws.rs.core.Response;

/** @author denys.davydov */
public class ACrudResource<E extends Persistent, D extends Dto> {
  private final AbstractCrudService<E> crudService;
  private final AMapper<E, D> mapper;

  ACrudResource(AbstractCrudService<E> crudService, AMapper<E, D> mapper) {
    this.crudService = crudService;
    this.mapper = mapper;
  }

  public Response post(final D inputDto) {
    cleanDtoIfNeed(inputDto);
    final E inputEntity = mapper.fromDto(inputDto);
    final E resultEntity = crudService.create(inputEntity);
    final D resultDto = mapper.toDto(resultEntity);
    return ResponseUtil.responseCreatedOrNot(resultDto);
  }

  public Response put(final Serializable id, final D inputDto) {
    cleanDtoIfNeed(inputDto);
    final E inputEntity = mapper.fromDto(inputDto);
    inputEntity.setId(id);
    final E resultEntity = crudService.update(inputEntity);
    final D resultDto = mapper.toDto(resultEntity);
    return ResponseUtil.responseOrNotFound(resultDto);
  }

  public Response get(final Long id) {
    final E entity = crudService.read(id);
    final D dto = mapper.toDto(entity);
    return ResponseUtil.responseOrNotFound(dto);
  }

  public Response delete(final Long id) {
    final E resultEntity = crudService.delete(id);
    final D resultDto = mapper.toDto(resultEntity);
    return ResponseUtil.responseOrNotFound(resultDto);
  }
}

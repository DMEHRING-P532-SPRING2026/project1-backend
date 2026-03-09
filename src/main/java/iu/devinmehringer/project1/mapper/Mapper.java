package iu.devinmehringer.project1.mapper;

public interface Mapper<E, Response> {
    Response toDTO(E entity);
}
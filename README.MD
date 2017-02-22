DtoMapper
=========

About
-----

DtoMapper is a library that allows to generate bean mappers.
It's a common problem (of 3 layers java web projects) that you have to write a lot of boilerplate mappers that convert entities to DTOs.

Mapper objects generated with DtoMapper are constructed with lib-asm, and their usage should not harm application's performance.

Right now (0.0.1-alpha) generated mapper maps only properties with same names and same type that have corresponding setters and getters.

Usage
-----

To map entity to DTO:

```java
MapperGenerator generator = new MapperGenerator();
IMapper<EntityClass, DtoClass> mapper = generator.generate(EntityClass.class, DtoClass.class);

DtoClass dto = mapper.map(entity);
```
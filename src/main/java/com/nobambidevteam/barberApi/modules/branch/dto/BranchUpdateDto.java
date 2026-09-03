package com.nobambidevteam.barberApi.modules.branch.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record BranchUpdateDto(

        @Size(min = 1, max = 255, message = "El nombre de sucursal no debe superar los 255 caracteres")
        String name,

        @Size(min = 1, max = 1000, message = "La dirección es demasiado larga")
        String address,

        @Size(max = 50, message = "El teléfono no puede superar los 50 caracteres")
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\.()]*$", message = "El formato del teléfono no es válido")
        String phone,

        String timezone,

        @URL(message = "Debe ser una URL válida")
        String mapIframeUrl,

        @URL(message = "Debe ser una URL válida")
        String googleMapsUrl,

        Boolean isActive
) {}

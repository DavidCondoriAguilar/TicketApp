package com.tickets.ravetix.util;

import com.tickets.ravetix.entity.Location;
import org.springframework.util.StringUtils;

public class MappingUtil {

    /**
     * Maps Location object to a formatted string
     */
    public static String mapLocationToString(Location location) {
        if (location == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        
        if (StringUtils.hasText(location.getDireccion())) {
            sb.append(location.getDireccion());
        }
        
        if (StringUtils.hasText(location.getCiudad())) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(location.getCiudad());
        }
        
        if (StringUtils.hasText(location.getPais())) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(location.getPais());
        }
        
        if (StringUtils.hasText(location.getCodigoPostal())) {
            if (sb.length() > 0) sb.append(" ");
            sb.append("(").append(location.getCodigoPostal()).append(")");
        }
        
        return sb.length() > 0 ? sb.toString() : null;
    }
}

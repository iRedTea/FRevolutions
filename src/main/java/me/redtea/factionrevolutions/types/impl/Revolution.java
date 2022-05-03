package me.redtea.factionrevolutions.types.impl;

import lombok.*;
import me.redtea.factionrevolutions.types.*;
import me.redtea.factionrevolutions.types.Data;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Revolution implements Data {

    private final UUID id;
    private String leader;
    private HashMap<String, Role> roles;
    private ArrayList<String> members;
}

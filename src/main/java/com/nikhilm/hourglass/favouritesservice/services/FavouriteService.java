package com.nikhilm.hourglass.favouritesservice.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface FavouriteService {

    public default  List<String> getParamList(String ids, String delim)    {
        return Arrays.stream(ids.split(delim)).collect(Collectors.toList());
    }
}

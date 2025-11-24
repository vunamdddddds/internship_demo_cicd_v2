package com.example.InternShip.config;

import com.example.InternShip.entity.PendingUser;
import com.example.InternShip.entity.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        modelMapper.typeMap(PendingUser.class, User.class)
                .addMappings(mapper -> mapper.skip(User::setId));
        return modelMapper;
    }
}

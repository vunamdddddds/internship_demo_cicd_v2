package com.example.InternShip.config;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Setter
public class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory {
    private AutowireCapableBeanFactory beanFactory;

    @NotNull
    @Override
    protected Object createJobInstance(@NotNull TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }
}

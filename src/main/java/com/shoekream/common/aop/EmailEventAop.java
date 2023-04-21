package com.shoekream.common.aop;

import com.shoekream.common.annotation.SendMail;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


@Aspect
@Component
public class EmailEventAop implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @AfterReturning(value = "@annotation(sendMail)", returning = "retVal")
    public void afterReturning(SendMail sendMail, Object retVal) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = sendMail.classInfo();
        if (clazz.isInstance(retVal)) {
            Constructor<?> constructor = clazz.getDeclaredConstructor(retVal.getClass());
            Object event = constructor.newInstance(retVal);
            eventPublisher.publishEvent(event);
        }
    }

}

package kkckkc.jsourcepad.util.command;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import javax.annotation.PostConstruct;
import java.util.List;

public class CommandMapperManagerImpl implements CommandMapperManager, BeanFactoryAware {
    private BeanFactory beanFactory;

    private List<CommandMapper> mappers;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void init() {
        
    }
    
    @Override
    public Command read(Object externalRepresentation) {
        for (CommandMapper mapper : mappers) {
            Command c = mapper.read(externalRepresentation);
            if (c != null) return c;
        }
        throw new RuntimeException("Cannot read command " + externalRepresentation); 
    }

    @Override
    public Object write(Command command) {
        for (CommandMapper mapper : mappers) {
            Object exRep = mapper.write(command);
            if (exRep != null) return exRep;
        }
        throw new RuntimeException("Cannot write command " + command);
    }
}

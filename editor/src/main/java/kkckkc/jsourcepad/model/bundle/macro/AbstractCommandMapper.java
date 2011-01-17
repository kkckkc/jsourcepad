package kkckkc.jsourcepad.model.bundle.macro;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AbstractCommandMapper implements MacroEncodingManager.Mapper {
    private static Logger logger = LoggerFactory.getLogger(AbstractCommandMapper.class);

    protected void doDecode(List<Mapping> mappings, Map<String, ?> textmate, Map<String, ?> jsourcepad) {
        for (Mapping mapping : mappings) {
            mapping.decode(textmate, jsourcepad);
        }
    }

    protected void doEncode(List<Mapping> mappings, Map<String, ?> jsourcepad, Map<String, ?> textmate) {
        for (Mapping mapping : mappings) {
            mapping.encode(textmate, jsourcepad);
        }
    }


    public static class Mapping {
        private String textmateId;
        private String jsourcepadId;
        private Mapper mapper;

        public Mapping(String textmateId, String jsourcepadId, Mapper mapper) {
            this.textmateId = textmateId;
            this.jsourcepadId = jsourcepadId;
            this.mapper = mapper;
            if (this.mapper == null) {
                this.mapper = IDENTITY;
            }
        }

        public Mapping(String textmateId, String jsourcepadId) {
            this(textmateId, jsourcepadId, null);
        }

        public void decode(Map<String, ?> textmate, Map<String, ?> jsourcepad) {
            if (textmateId == null) return;
            if (jsourcepadId == null) {
                logger.warn("Macro field " + textmateId + " not supported");
                return;
            }

            Object o = textmate.get(textmateId);
            ((Map) jsourcepad).put(jsourcepadId, mapper.fromTextMate(o));
        }

        public void encode(Map<String, ?> textmate, Map<String, ?> jsourcepad) {
            if (jsourcepadId == null) return;
            Object o = jsourcepad.get(jsourcepadId);
            ((Map) textmate).put(textmateId, mapper.toTextMate(o));
        }
    }


    public interface Mapper<T, J> {
        public J fromTextMate(T t);
        public T toTextMate(J j);
    }

    public static Mapper<Boolean, Boolean> INVERT = new Mapper<Boolean, Boolean>() {
        @Override
        public Boolean fromTextMate(Boolean aBoolean) {
            return ! aBoolean;
        }

        @Override
        public Boolean toTextMate(Boolean aBoolean) {
            return ! aBoolean;
        }
    };

    public static Mapper<Object, Object> IDENTITY = new Mapper<Object, Object>() {
        @Override
        public Object fromTextMate(Object o) {
            return o;
        }

        @Override
        public Object toTextMate(Object o) {
            return o;
        }
    };
}

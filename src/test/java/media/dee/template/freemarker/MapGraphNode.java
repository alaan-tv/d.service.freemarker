package media.dee.template.freemarker;

import media.dee.dcms.core.GraphNode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapGraphNode extends HashMap<String, Object> implements GraphNode {
    private Collection<String> labels = Collections.emptySet();
    private long rawid;

    public MapGraphNode(long rawid, Collection<String> labes, Map<String, Object> map){
        super(map);
        this.labels = labes;
        this.rawid = rawid;
    }

    @Override
    public Collection<String> getLabels() {
        return null;
    }

    @Override
    public long getRawId() {
        return this.rawid;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T)super.get(key);
    }

    @Override
    public boolean contains(String key) {
        return super.containsKey(key);
    }
}

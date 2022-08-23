package InputOutput;

import java.io.Serial;
import java.io.Serializable;

public class SerializableItem implements Serializable {
    private final int id;
    private final String tag;

    @Serial
    private static final long serialVersionUID = 1L;

    public SerializableItem(int id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

}

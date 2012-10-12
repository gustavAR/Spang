package serialization;

import utils.Packer;
import utils.UnPacker;

public interface ISerializer {
	public void serialize(Packer packer, Object message);
	public Object deserialize(UnPacker unpacker);
	public Class<?> getSerializableType();
}

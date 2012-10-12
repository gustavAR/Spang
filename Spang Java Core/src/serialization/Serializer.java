package serialization;

import utils.Packer;
import utils.UnPacker;

public abstract class Serializer<T> implements ISerializer {
	protected abstract void serializeInternal(Packer packer, T message);
	public abstract T deserialize(UnPacker unpacker);
	
	@SuppressWarnings("unchecked") //Silly java why you so silly?
	public void serialize(Packer packer, Object message) {
		this.serializeInternal(packer, (T)message);
	}
}

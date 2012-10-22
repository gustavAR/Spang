package serialization;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class SerializeManagerTests {

	private SerializeManager manager;
	
	@Before
	public void setup() {
		this.manager = new SerializeManager();
	}
	
	
	@Test
	public void testRegisterSerializer() {
		manager.registerSerilizer(new StringSerializer());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIfExceptionIsThrownOnDuplicateSerializer() {
		manager.registerSerilizer(new StringSerializer());
		manager.registerSerilizer(new StringSerializer());
	}
}
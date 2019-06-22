package pay.util.jdpayutil;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class CertUtil {

    /**
	 * 效率：以文件名为Key缓存证书
	 */
	private static final ConcurrentMap<String, String> certs = new ConcurrentHashMap<String, String>();

	public static String getCert() {
		String certPath = "";
		if (certPath == null || certPath.equals("")) {
			return null;
		}
		String cert = certs.get(certPath);

		if (cert == null || cert.equals("")) {
			byte[] strBuffer = null;
			int flen = 0;
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(certPath);
			if (is != null) {
				try {
					flen = (int) is.available();
					strBuffer = new byte[flen];
					is.read(strBuffer, 0, flen);
					cert = Base64.encode(strBuffer);
					certs.put(certPath, cert);
				} catch (FileNotFoundException e) {
					log.error("getCert Exception",e);
				} catch (IOException e) {
					log.error("getCert Exception",e);
				}
			}
		}
		return cert;
	}

	public static void main(String[] args) {
		System.out.println(certs.get(null));
	}
}
package org.opengeoreviews.opendb.ops.auth;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.prng.FixedSecureRandom;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.opengeoreviews.opendb.ops.IOpenDBOperation;
import org.opengeoreviews.opendb.ops.OpDefinitionBean;
import org.opengeoreviews.opendb.ops.OpenDBOperation;
import org.opengeoreviews.opendb.ops.OperationsRegistry;
import org.springframework.jdbc.core.JdbcTemplate;

@OpenDBOperation(LoginOperation.OP_ID)
public class LoginOperation implements IOpenDBOperation {

	protected static final Log LOGGER = LogFactory.getLog(LoginOperation.class);
	
	public static final String OP_ID = "login";
	private OpDefinitionBean definition;

	@Override
	public String getName() {
		return OP_ID;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean prepare(OpDefinitionBean definition, StringBuilder errorMessage) {
		this.definition = definition;
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute(JdbcTemplate template, StringBuilder errorMessage) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OpDefinitionBean getDefinition() {
		return definition;
	}

	
	@Override
	public String getType() {
		return OperationsRegistry.OP_TYPE_AUTH;
	}

    public static void main(String[] args) throws Exception {

        KeyPair keyPair = getKeyPair();

        // "8HEk28FDqegMkrrp";
        byte[] data = "test".getBytes("UTF8");

        Signature sig = Signature.getInstance("SHA1withECDSA");
        sig.initSign(keyPair.getPrivate());
        sig.update(data);
        byte[] signatureBytes = sig.sign();
        System.out.println("Signature:" + Arrays.toString(signatureBytes));

        
        sig = Signature.getInstance("SHA1withECDSA");
        sig.initVerify(keyPair.getPublic());
        sig.update(data);
        

        System.out.println(sig.verify(signatureBytes));
    }

    private static KeyPair getKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
//        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//        ECKeyPairGenerator pGen = new ECKeyPairGenerator();
//        pGen.generateKeyPair();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        
        byte[] bytes = "Hello world  + saltqwerqwerqwerqwer1231232112312321312".
        		getBytes();
        System.out.println(System.currentTimeMillis());
        // "pwd__scrypt_nick_17_8_1_256__ec_secp256k1"
        byte[] scrypt = SCrypt.generate(bytes, "vshcherb".getBytes(), 1 << 17, 8, 1, 256);
        System.out.println(System.currentTimeMillis());
        kpg.initialize(ecSpec, new FixedSecureRandom(scrypt));
//        kpg.initialize(1024, new SecureRandom("Hello world  + salt".getBytes()));
        System.out.println(Arrays.toString(kpg.genKeyPair().getPublic().getEncoded()));
        System.out.println(Arrays.toString(kpg.genKeyPair().getPrivate().getEncoded()));
//        kpg.initialize(1024);
        return kpg.genKeyPair();
    }
}

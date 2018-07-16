import com.wavesplatform.wavesj.*;

import java.io.IOException;
import java.net.URISyntaxException;


public class MakeScriptExample {
    public static void main(String[] args) throws IOException, URISyntaxException {
        ///
        final long FEE = 1000000;
        //final long SCRIPT_FEE = 1000000;
        String AliceSeed = "house horse basket hot ball honey health myself silly december endless rent faculty report beyond";
        String BobSeed = "lazy health fix lens salad dwarf myself breeze december silly rent endless report faculty beyond";
        String CooperSeed = "basket health fix lens salad dwarf myself breeze december silly rent endless report faculty beyond";

        PrivateKeyAccount alice = PrivateKeyAccount.fromSeed(AliceSeed, 0, Account.TESTNET);
        PrivateKeyAccount bob = PrivateKeyAccount.fromSeed(BobSeed,0, Account.TESTNET);
        PrivateKeyAccount cooper= PrivateKeyAccount.fromSeed(CooperSeed,0, Account.TESTNET);



        String aliceAddress = alice.getAddress();
        String bobAddress = bob.getAddress();
        String cooperAddress = cooper.getAddress();

        System.out.println("alice PK" + Base58.encode(alice.getPublicKey()));
        System.out.println("bob PK" + Base58.encode(bob.getPublicKey()));

        Node node = new Node("https://testnode2.wavesnodes.com");

        String script = "let alicePubKey  = base58'CHeZYMCG2TuRbtQbpGZEPn9yaTRi8hNJXaJkoX7eLyv';" +
                "let bobPubKey = base58'3aRpjNJvuSodTHFwTAUvWfFxtyp4N2UJqbh2baigr2Dx';" +
                "let cooperPubKey = base58'3Mw2hDQoFraoUgnfz7qJu5qJ1XSsisgU2EJ';" +
                "let aliceSigned = if(sigVerify(tx.bodyBytes, tx.proofs[0], alicePubKey)) then 1 else 0;" +
                "let bobSigned = if(sigVerify(tx.bodyBytes, tx.proofs[1], bobPubKey)) then 1 else 0;" +
                "let cooperSigned = if(sigVerify(tx.bodyBytes, tx.proofs[2], cooperPubKey)) then 1 else 0;" +
                "aliceSigned + bobSigned + cooperSigned >= 2";
        String bytecode = node.compileScript(script);
        System.out.println(bytecode);

        //Transaction stx = Transaction.makeScriptTx(cooper,bytecode, Account.TESTNET, 400000);
        //node.send(stx);

        Transaction tx1 = Transaction.makeTransferTx(cooper, bobAddress, 1,"WAVES", FEE * 4 ,"WAVES", "Sending currency");
        String aliceSig =alice.sign(tx1);
        String bobSig = bob.sign(tx1);

        tx1= tx1.withProof(0,aliceSig);
        tx1 = tx1.withProof(1, bobSig);

        String txid = node.send(tx1);
        System.out.println(txid);

        System.out.print("Alice Address" + aliceAddress);
        System.out.print("Bob Address" + bobAddress);
        System.out.print("Cooper Address" + cooperAddress);




    }

}

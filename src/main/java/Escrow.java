import com.wavesplatform.wavesj.*;
import java.io.IOException;
import java.net.URISyntaxException;


public class Escrow {
    public static void main(String[] args) throws IOException, URISyntaxException {

        //fee
        final long FEE = 1000000;

        // Set testnet node
        Node node = new Node("https://testnode2.wavesnodes.com");

        //Set seeds for 2 accounts
        String SellerSeed = "lazy health fix lens salad dwarf myself breeze december silly rent endless report faculty beyond";

        String EscrowSeed = "basket health fix lens salad dwarf myself breeze december silly rent endless report faculty beyond";

        //Get Private keys
        PrivateKeyAccount buyer = PrivateKeyAccount.fromPrivateKey("EW8VJkEfqr1nW835vKWBqWGeAZdLm8hN7MWf9ZePKr1y", Account.TESTNET);
        PrivateKeyAccount seller = PrivateKeyAccount.fromSeed(SellerSeed,0, Account.TESTNET);
        PrivateKeyAccount escrow= PrivateKeyAccount.fromSeed(EscrowSeed,0, Account.TESTNET);

        //Generating random account
        String newAccountSeed = PrivateKeyAccount.generateSeed();
        PrivateKeyAccount newAccount = PrivateKeyAccount.fromSeed(newAccountSeed, 0, Account.TESTNET);
        String newAccountAddress = newAccount.getAddress();
        System.out.print("Escrow Address: "  + newAccountAddress + "" + "\n");

        Transaction tx2 = Transaction.makeTransferTx(buyer, newAccountAddress, 100000000,"WAVES", FEE * 4 ,"WAVES", "Sending Waves");
        node.send(tx2);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Get Addresses and print them
        String buyerAddress = buyer.getAddress();
        String sellerAddress = seller.getAddress();
        //String escrowAddress = escrow.getAddress();
        System.out.println("buyerPK: " + Base58.encode(buyer.getPublicKey()));
        System.out.println("sellerPK: " + Base58.encode(seller.getPublicKey()));


        // Set Script and compile it
        String script = "let buyerPubKey  = base58'" + Base58.encode(buyer.getPublicKey()) + "';" +
                "let sellerPubKey = base58'" + Base58.encode(seller.getPublicKey()) + "';" +
                "let escrowPubKey = base58'" + Base58.encode(escrow.getPublicKey()) + "';" +
                "let buyerSigned = if(sigVerify(tx.bodyBytes, tx.proofs[0], buyerPubKey)) then 1 else 0;" +
                "let sellerSigned = if(sigVerify(tx.bodyBytes, tx.proofs[1], sellerPubKey)) then 1 else 0;" +
                "let escrowSigned = if(sigVerify(tx.bodyBytes, tx.proofs[2], escrowPubKey)) then 1 else 0;" +
                "buyerSigned + sellerSigned + escrowSigned >= 2";
        String bytecode = node.compileScript(script);
        System.out.println(bytecode);

        //newAccount Make Script transaction and send it to the network
        Transaction stx = Transaction.makeScriptTx(newAccount,bytecode, Account.TESTNET, 100000);
        node.send(stx);

        // time required for the transaction to be included into the blockchain
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Buyer make a transfer
        Transaction tx1 = Transaction.makeTransferTx(newAccount, sellerAddress, 1,"WAVES", FEE * 4 ,"WAVES", "Sending currency");

        //Buyer and Escrow sign the deal with proofs
        String buyerSig =  buyer.sign(tx1);
        String escrowSig = escrow.sign(tx1);
        tx1 = tx1.withProof(0,buyerSig);
        tx1 = tx1.withProof(2, escrowSig);

        // Send the transfer transaction ot the network and print the Tx Id
        String txid = node.send(tx1);
        System.out.println(txid);

        //print all addresses
        System.out.print("Buyer Address: "  + buyerAddress + "\n");
        System.out.print("Seller Address: " + sellerAddress);
        System.out.print("Escrow Address: " + newAccountAddress);




    }

}

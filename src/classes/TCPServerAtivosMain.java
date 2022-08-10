package classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServerAtivosMain extends Thread {

    private List<TCPServerConnection> clientes;
    private ServerSocket server;
    int[][] jogo = {{0, 0, 0}, 
                    {0, 0, 0}, 
                    {0, 0, 0}};
    ArrayList<String> tipos = new ArrayList<>();
    private int socketsNum;
    boolean gameOver = false;

    public TCPServerAtivosMain(int porta) throws IOException {
        this.server = new ServerSocket(porta);
        System.out.println(this.getClass().getSimpleName() + " rodando na porta: " + server.getLocalPort());
        this.clientes = new ArrayList<>();
        this.socketsNum = 0;
        tipos.add("X");
        tipos.add("O");
    }

    @Override
    public void run() {
        Socket socket;
        while (true) {
            if (socketsNum < 2) {
                try {
                    socket = this.server.accept();
                    TCPServerConnection cliente = new TCPServerConnection(socket, tipos.remove(0));
                    socketsNum++;
                    novoCliente(cliente);
                    (new TCPServerAtivosHandler(cliente, this)).start();
                } catch (IOException ex) {
                    System.out.println("Erro 4: " + ex.getMessage());
                }
            }

        }
    }
    


    public synchronized void novoCliente(TCPServerConnection cliente) throws IOException {
        clientes.add(cliente);
    }

    public synchronized void removerCliente(TCPServerConnection cliente) {
        clientes.remove(cliente);
        try {
            cliente.getInput().close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        cliente.getOutput().close();
        try {
            cliente.getSocket().close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List getClientes() {
        return clientes;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.server.close();
    }

}

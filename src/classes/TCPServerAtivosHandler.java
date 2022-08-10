package classes;

import java.io.IOException;
import java.util.List;

public class TCPServerAtivosHandler extends Thread {

    private TCPServerConnection cliente;
    private TCPServerAtivosMain caller;
    int turno = 0;

    public TCPServerAtivosHandler(TCPServerConnection cliente, TCPServerAtivosMain caller) throws IOException {
        this.cliente = cliente;
        this.caller = caller;
    }

    @Override
    protected void finalize() throws Throwable {
        encerrar();
    }

    private void encerrar() {
        this.caller.removerCliente(this.cliente);
    }

    public synchronized void messageDispatcher(String message) throws IOException {
        List<TCPServerConnection> clientes = this.caller.getClientes();
        for (TCPServerConnection cli : clientes) {
            if (cli.getSocket() != null && cli.getSocket().isConnected() && cli.getOutput() != null) {
                cli.getOutput().println(message);
                cli.getOutput().flush();
            }
        }
    }

    public synchronized void messageToOne(String message) throws IOException {

        if (cliente.getSocket() != null && cliente.getSocket().isConnected() && cliente.getOutput() != null) {
            cliente.getOutput().println(message);
            cliente.getOutput().flush();

        }
    }

    private synchronized String processar(String message) {
        int i = Integer.parseInt(String.valueOf(message.charAt(0)));
        int j = Integer.parseInt(String.valueOf(message.charAt(2)));
        if ("X".equals(cliente.getTipo())) {
            caller.jogo[i][j] = 1;
        } else if ("O".equals(cliente.getTipo())) {
            caller.jogo[i][j] = 2;
        }
        message = "J" + "|" + i + "|" + j + "|" + cliente.getTipo();

        return message;
    }

    public synchronized void reiniciarJogo() throws IOException {
        if ("O".equals(cliente.getTipo())) {
            cliente.setTipo("X");
        } else if ("X".equals(cliente.getTipo())) {
            cliente.setTipo("O");
        }
        int turno = 0;

        for (int i = 0; i < caller.jogo.length; i++) {
            for (int j = 0; j < caller.jogo[0].length; j++) {
                caller.jogo[i][j] = 0;
            }
        }

    }

    private synchronized String fimDoJogo() {
        String message = "";
        if (caller.jogo[0][0] == 1 && caller.jogo[0][1] == 1 && caller.jogo[0][2] == 1) {
            caller.gameOver = true;
            message = "FIM" + "X";
        } else if (caller.jogo[1][0] == 1 && caller.jogo[1][1] == 1 && caller.jogo[1][2] == 1) {
            caller.gameOver = true;
            message = "FIM" + "X";
        } else if (caller.jogo[2][0] == 1 && caller.jogo[2][1] == 1 && caller.jogo[2][2] == 1) {
            caller.gameOver = true;
            message = "FIM" + "X";
        } else if (caller.jogo[0][0] == 1 && caller.jogo[1][0] == 1 && caller.jogo[2][0] == 1) {
            caller.gameOver = true;
            message = "FIM" + "X";
        } else if (caller.jogo[0][1] == 1 && caller.jogo[1][1] == 1 && caller.jogo[2][1] == 1) {
            caller.gameOver = true;
            message = "FIM" + "X";
        } else if (caller.jogo[0][2] == 1 && caller.jogo[1][2] == 1 && caller.jogo[2][2] == 1) {
            caller.gameOver = true;
            message = "FIM" + "X";
        } else if (caller.jogo[0][0] == 1 && caller.jogo[1][1] == 1 && caller.jogo[2][2] == 1) {
            caller.gameOver = true;
            message = "FIM" + "X";
        } else if (caller.jogo[0][2] == 1 && caller.jogo[1][1] == 1 && caller.jogo[2][0] == 1) {
            caller.gameOver = true;
            message = "FIM" + "X";
        } else if (caller.jogo[0][0] == 2 && caller.jogo[0][1] == 2 && caller.jogo[0][2] == 2) {
            caller.gameOver = true;
            message = "FIM" + "O";
        } else if (caller.jogo[1][0] == 2 && caller.jogo[1][1] == 2 && caller.jogo[1][2] == 2) {
            caller.gameOver = true;
            message = "FIM" + "O";
        } else if (caller.jogo[2][0] == 2 && caller.jogo[2][1] == 2 && caller.jogo[2][2] == 2) {
            caller.gameOver = true;
            message = "FIM" + "O";
        } else if (caller.jogo[0][0] == 2 && caller.jogo[1][0] == 2 && caller.jogo[2][0] == 2) {
            caller.gameOver = true;
            message = "FIM" + "O";
        } else if (caller.jogo[0][1] == 2 && caller.jogo[1][1] == 2 && caller.jogo[2][1] == 2) {
            caller.gameOver = true;
            message = "FIM" + "O";
        } else if (caller.jogo[0][2] == 2 && caller.jogo[1][2] == 2 && caller.jogo[2][2] == 2) {
            caller.gameOver = true;
            message = "FIM" + "O";
        } else if (caller.jogo[0][0] == 2 && caller.jogo[1][1] == 2 && caller.jogo[2][2] == 2) {
            caller.gameOver = true;
            message = "FIM" + "O";
        } else if (caller.jogo[0][2] == 2 && caller.jogo[1][1] == 2 && caller.jogo[2][0] == 2) {
            caller.gameOver = true;
            message = "FIM" + "O";
        } else {
            int count = 0;
            for (int m = 0; m < caller.jogo.length; m++) {
                for (int n = 0; n < caller.jogo[0].length; n++) {
                    if(caller.jogo[m][n] != 0){
                        count ++;
                    }
                }

            }
            

            if (count == 9) {
                message = "FIM" + "-";
                caller.gameOver = true;
            }
        }
        return message;
    }

    @Override
    public void run() {
        String message;
        try {
            while (caller.getClientes().size() != 2) {
                message = "Aguardando jogador";
                messageDispatcher(message);
                Thread.sleep(5);

            }
            while (true) {
                message = cliente.getTipo();
                messageToOne(message);

                while (true) {
                    message = this.cliente.getInput().readLine();
                    if ("Reiniciar".equals(message)) {
                        reiniciarJogo();
                        caller.gameOver = false;
                        Thread.sleep(100);
                        break;
                    } else {
                        message = processar(message);
                        messageDispatcher(message);
                        message = fimDoJogo();
                        if (caller.gameOver) {
                            messageDispatcher(message);
                            Thread.sleep(100);

                        }

                    }

                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}

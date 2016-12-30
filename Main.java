import java.io.*;
import java.nio.file.*;
import java.nio.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
		public static String host = "localhost";
		public static int port = 31337;
		public static String character = "A";
    
        public static void main(final String[] args) {
	        final Scanner scanner = new Scanner(System.in);
	        if(args.length < 2) {
		        System.out.print("ip:");
		        host = scanner.nextLine();
		        System.out.print("port:");
		        port = Integer.parseInt(scanner.nextLine());
	        } else {
	            host = args[0];
	            port = Integer.parseInt(args[1]);
                
                if(args.length == 3) {
                System.out.println("Starting 1337 mode...");
                    try{specialMode();}catch(Exception e){e.printStackTrace();}
                    return;
                }
	        }
            try {
                final Socket socket = new Socket(host, port);
                System.out.println("Established connection with " + host + ":" + port + "...");
                
                Thread read = new Thread(new Runnable() {public void run() {
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    } catch (IOException e2) {
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            System.exit(1);
                        }
                        scanner.close();
                        System.err.println("[Error] Failed to open input stream.");
                        System.exit(1);
                    }
                    String str = "";
                    try {
                        while ((str = br.readLine()) != null) {
                            System.out.println(str);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    
                    try {
                        br.close();
                    } catch (IOException e) {
                        System.err.println("[Error] Socket closed.");
                        System.exit(1);
                    }
                }});
                
                Thread write = new Thread(new Runnable() {public void run() {
                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(socket.getOutputStream(), true);
                    } catch (IOException e) {
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        scanner.close();
                        System.err.println("[Error] Failed to open output stream.");
                        System.exit(1);
                    }
                    
                    while(true) {
			    String x = scanner.nextLine();
			    if(x.startsWith("^")) {
				    if(x.equalsIgnoreCase("^re")) {
					    Main.main(args);
				    } else {
					    System.out.println("Unknown command. Try ^re to reconnect.");
				    }
			    } else {
		                writer.println(x);
		                System.out.println("[Sent!]");
			    }
                }
            }});
            
            read.start();
            write.start();
        } catch(Exception e) {
            System.err.println("[Error] Unable to establish connection.");
            System.exit(1);
        }
    }
    
    public static void specialMode() throws Exception {
	        final Scanner scanner = new Scanner(System.in);
    try {
                Thread write = new Thread(new Runnable() {
                            BufferedReader br = null;
                            public void run(){
                            try {
    Socket socket = new Socket(host, port);
                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(socket.getOutputStream(), true);
                    } catch (IOException e) {
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        scanner.close();
                        System.err.println("[Error] Failed to open output stream.");
                        System.exit(1);
                    }
                    int i = 1;
                    while(true) {
			            System.out.print("rwsocket>");
			            String cmd = scanner.nextLine();
			            if(cmd.equalsIgnoreCase("payload"))
			                System.out.println(new String(new char[i]).replaceAll("\0", character) + "\n");
			            else if(cmd.equalsIgnoreCase("strlen"))
			                System.out.println(i + "\n");
			            else if(cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("?"))
			                System.out.println("[Commands]\nhelp - Displays this menu\nstrlen - Display the length of the current payload\npayload - Display current payload\njump <number> - set length of payload\n");
			            else if(cmd.toLowerCase().startsWith("jump")) {
			                String[] args = cmd.split(" ");
			                if(args.length == 1)
			                    System.out.println("Number argument missing.\n");
			                else {
			                    try {
        			                i = Integer.parseInt(args[1]);
        			                System.out.println("Payload length is now " + args[1] + "\n");
			                    } catch(Exception e) {
			                        System.out.println("Invalid number.\n");
			                    }
			                }
			            } else if(cmd.toLowerCase().startsWith("file")) {
			                String[] args = cmd.split(" ");
			                if(args.length == 1)
			                    System.out.println("String argument missing.\n");
			                else {
			                    try {
        			                String content = new String(Files.readAllBytes(Paths.get(new File(args[1]).getAbsolutePath())));
        			                
        			                writer.println(content);
	                        
	                        socket.close();
                            socket = new Socket(host, port);
	                        
	                        try {
                                writer = new PrintWriter(socket.getOutputStream(), true);
                            } catch (IOException e) {
                                try {
                                    socket.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                scanner.close();
                                System.err.println("[Error] Failed to open output stream.");
                                System.exit(1);
                            }
                            try {
                                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            } catch (IOException e2) {
                                try {
                                    socket.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                    System.exit(1);
                                }
                                scanner.close();
                                System.err.println("[Error] Failed to open input stream.");
                                System.exit(1);
                            }
                            String str = "";
                            try {
                                while (br.ready()) {
                                    str = br.readLine();
                                    /*if(str.contains("Closing")) {
                                        System.out.println("[debug] skipping");
                                        break;}*/
                                    System.out.println(str);
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
	                        
			                i++;
			                    } catch(Exception e) {
			                        e.printStackTrace();
			                        System.out.println("Unknown error.\n");
			                    }
			                }
			            } else if(cmd.length() > 0) 
			                System.out.println("Unknown command. Try typing `help` for the help menu.\n");
		                else {
			                
			                System.out.print(String.format("\033[%dA",1));
                            System.out.print("\033[2K"); // Erase line content
			                String payload = new String(new char[i]).replaceAll("\0", character);
	                        writer.println(payload);
	                        
	                        socket.close();
                            socket = new Socket(host, port);
	                        
	                        try {
                                writer = new PrintWriter(socket.getOutputStream(), true);
                            } catch (IOException e) {
                                try {
                                    socket.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                scanner.close();
                                System.err.println("[Error] Failed to open output stream.");
                                System.exit(1);
                            }
                            try {
                                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            } catch (IOException e2) {
                                try {
                                    socket.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                    System.exit(1);
                                }
                                scanner.close();
                                System.err.println("[Error] Failed to open input stream.");
                                System.exit(1);
                            }
                            String str = "";
                            try {
                                while (br.ready()) {
                                    str = br.readLine();
                                    /*if(str.contains("Closing")) {
                                        System.out.println("[debug] skipping");
                                        break;}*/
                                    System.out.println(str);
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
	                        
			                i++;
			            }
			            } 
                    }catch(Exception e) {e.printStackTrace();}
            }});
            
            write.start();
        } catch(Exception e) {
            System.err.println("[Error] Unable to establish connection.");
            System.exit(1);
        }
    }
}

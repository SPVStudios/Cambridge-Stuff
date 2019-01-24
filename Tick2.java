package uk.ac.cam.cl.gfxintro.spv28.tick2;

public class Tick2 {
    public static final String DEFAULT_INPUT = "resources/mtsthelens.png";

    public static void usageError() {
        System.err.println("USAGE: <tick3> --input INPUT [--output OUTPUT]");
        System.exit(-1);
    }

    public static void main(String[] args) {
        // We should have an even number of arguments
        if (args.length % 2 != 0)
            usageError();

        String input = DEFAULT_INPUT, output = null;
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
            case "-i":
            case "--input":
                input = args[i + 1];
                break;
            case "-o":
            case "--output":
                output = args[i + 1];
                break;
            default:
                System.err.println("unknown option: " + args[i]);
                usageError();
            }
        }

        if (input == null) {
            System.err.println("required arguments not present");
            usageError();
        }

        OpenGLApplication app = null;
        try {
            app = new OpenGLApplication(input);

            if (output != null) {
                app.initializeOpenGL();
                app.takeScreenshot(output);
            } else {
                app.run();
            }
        } catch( RuntimeException ex ) {
            System.err.println( "RuntimeException: " + ex.getMessage() );
        } finally {
            if (app != null)
                app.stop();
        }
    }
}

package me.gking2224.common.cli;

import static java.lang.String.format;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineHelper {
    
    private static final Logger LOG = LoggerFactory.getLogger(CommandLineHelper.class);
    
    private static final String DEFAULT_ENV = "embedded";
    private static final File DEFAULT_PROPS_DIR = new File(System.getProperty("user.home"), "properties");

    private static final String ENV = "e";
    private static final String APP = "a";
    private static final String PROPS_DIR = "p";

    public static Options getOptions() {
        Options o = new Options();
        Option app = Option.builder(APP).argName("application").desc("The application(s) to run (batch, web, jmx)").hasArgs().required().longOpt("app").build();
        Option env = Option.builder(ENV).argName("environment").desc("The environment being run").hasArg().longOpt("env").build();
        Option propsDir = Option.builder(PROPS_DIR).argName("propsDir").desc("External properties directory").required(false).hasArg().longOpt("props-dir").build();
        o.addOption(env);
        o.addOption(app);
        o.addOption(propsDir);
        return o;
    }
    
    public static CommandLine parseCommandLine(String name, String[] args) {
        CommandLineParser parser = new DefaultParser();
        org.apache.commons.cli.CommandLine cl = null;
        Options options = getOptions();
        try {
            cl = parser.parse(options, args);
        } catch (ParseException e) {
            LOG.error("Could not parse commandline args {} ", args, e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( name, options );
            return null;
        }
        return new CommandLine(cl);
    }

    public static class CommandLine {
        private org.apache.commons.cli.CommandLine cl;

        public CommandLine(org.apache.commons.cli.CommandLine cl) {
            this.cl = cl;
        }
        
        public String getEnv() {
            String env = cl.getOptionValue(ENV);
            if (env == null) env = DEFAULT_ENV;
            return env;
        }
        
        public File getPropsDir() {
            String propsDirStr = cl.getOptionValue(PROPS_DIR);
            File propsDir = null;
            if (propsDirStr != null) {
                propsDir = new File(propsDirStr);
                if (!propsDir.exists()) {
                    throw new RuntimeException(format("%s does not exist", propsDir.getAbsolutePath()));
                }
                if (!propsDir.isDirectory()) {
                    throw new RuntimeException(format("%s is not a directory", propsDir.getAbsolutePath()));
                }
            }
            return propsDir;
        }
        
        public Set<String> getApps() {
            String[] optionValues = cl.getOptionValues(APP);
            return new HashSet<String>(Arrays.asList(optionValues));
        }
    }
}

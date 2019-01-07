package org.contentmine.ami.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDictionaryTool.RawFileFormat;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.euclid.Util;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Reusable Commands for picocli CommandLine
 * see Picocli manual
 * 
 * @author pm286
 *
 */
@Command(
	addMethodSubcommands = false,
			//String separator() default "=";
	separator = "=",
			//String[] version() default {};
	mixinStandardHelpOptions = true,
			//boolean helpCommand() default false;
	helpCommand = true,
			//String headerHeading() default "";
	abbreviateSynopsis = true,
			//String[] customSynopsis() default {};
	descriptionHeading = "Description\n===========\n",
			//String[] description() default {};
	parameterListHeading  = "Parameters\n=========\n",
			//String optionListHeading() default "";
	optionListHeading  = "Options\n=======\n",
			//boolean sortOptions() default true;
	sortOptions = true,
			//char requiredOptionMarker() default ' ';
	requiredOptionMarker = '*',
			//Class<? extends IDefaultValueProvider> defaultValueProvider() default NoDefaultProvider.class;
	showDefaultValues = true,
			//String commandListHeading() default "Commands:%n";
	commandListHeading = "Commands:%n=========%n",
			//String footerHeading() default "";
	hidden = false,
			//String resourceBundle() default "";
	usageHelpWidth = 80
	)

public abstract class AbstractAMITool implements Callable<Void> {
	private static final Logger LOG = Logger.getLogger(AbstractAMITool.class);

	protected static final String NONE = "NONE";
	static {
		LOG.setLevel(Level.DEBUG);
	}


    @Option(names = {"-p", "--cproject"}, 
		arity = "0..1",
		paramLabel="CProject",
		description = "CProject (directory) to process")
    protected String cProjectDirectory = null;

    @Option(names = {"-t", "--ctree"}, 
		arity = "0..1",
		paramLabel = "CTree",
		interactive = false,
		descriptionKey = "descriptionKey",
		description = "single CTree (directory) to process")
    protected String cTreeDirectory = null;

    @Option(names = {"--basename"}, 
    		arity="1",
    		description = "User's basename for outputfiles (e.g. foo/bar/<basename>.png. By default this is computed by AMI."
    				+ " This allows users to create their own variants, but they won't be known by default to subsequent"
    				+ "applications"
    		)
	protected String userBasename;

    @Option(names = {"--excludetree"}, 
    		arity="1..*",
    		description = "exclude the CTrees in the list. (only works with --cproject). "
    				+ "Currently must be explicit but we'll add globbing later."
    		)
	public String[] excludeTrees;

    @Option(names = {"--includetree"}, 
    		arity="1..*",
    		description = "include only the CTrees in the list. (only works with --cproject). "
    				+ "Currently must be explicit but we'll add globbing later."
    		)
	public String[] includeTrees;

    @Option(names = {"--log4j"}, 
    		arity="2",
    		description = "format: <classname> <level>; sets logging level of class, e.g. \n "
    				+ "org.contentmine.ami.lookups.WikipediaDictionary INFO"
    		)
	public String[] log4j;

	@Option(names = {"--rawfiletypes" }, 
			arity = "1..*", 
			split = ",", 
			description = "suffixes of included files (${COMPLETION-CANDIDATES}): "
					+ "can be concatenated with commas ")
	protected RawFileFormat[] rawFileFormats;

	@Option(names = { "-v", "--verbose" }, 
    		description = {
        "Specify multiple -v options to increase verbosity.",
        "For example, `-v -v -v` or `-vvv`"
        + "We map ERROR or WARN -> 0 (i.e. always print), INFO -> 1(-v), DEBUG->2 (-vv)" })
    protected boolean[] verbosity = new boolean[0];
    
	protected static File HOME_DIR = new File(System.getProperty("user.home"));
	protected static String CONTENT_MINE_HOME = "ContentMine";
	protected static File DEFAULT_CONTENT_MINE_DIR = new File(HOME_DIR, CONTENT_MINE_HOME);
	
	protected CProject cProject;
	protected CTree cTree;
	protected CTreeList cTreeList;
	// needed for testing I think
	protected File cProjectOutputDir;
	protected File cTreeOutputDir;
	
	protected String[] args;
	private Level level;
	protected File contentMineDir = DEFAULT_CONTENT_MINE_DIR;


	public void init() {
	}

	public void runCommands(String cmd) {
		String[] args = cmd == null ? new String[]{} : cmd.trim().split("\\s+");
		runCommands(args);
	}
	
	/** parse commands and pass to CommandLine
	 * calls CommandLine.call(this, args)
	 * 
	 * @param args
	 */
	public void runCommands(String[] args) {
		this.args = args;
		// add help
    	args = args.length == 0 ? new String[] {"--help"} : args;
		CommandLine.call(this, args);
		
    	printGenericHeader();
		parseGenerics();
		
    	printSpecificHeader();
		parseSpecifics();
		
		if (level != null && !Level.WARN.isGreaterOrEqual(level)) {
			System.err.println("processing halted due to argument errors, level:"+level);
		} else {
			runGenerics();
			runSpecifics();
		} 
	}
	
	protected abstract void parseSpecifics();
	protected abstract void runSpecifics();

	protected boolean parseGenerics() {
		validateCProject();
		validateCTree();
		validateRawFormats();
    	setLogging();
    	printGenericValues();
        return true;
	}

	private void setLogging() {
		if (log4j != null) {
			Map<Class<?>, Level> levelByClass = new HashMap<Class<?>, Level>();
			for (int i = 0; i < log4j.length; ) {
				String className = log4j[i++];
				Class<?> logClass = null;
				try {
					logClass = Class.forName(className);
				} catch (ClassNotFoundException e) {
					LOG.error("Cannot find logger Class: "+logClass);
					continue;
				}
				String levelS = log4j[i++];
				Level level =  Level.toLevel(levelS);
				if (level == null) {
					LOG.error("cannot parse class/level: "+className+":"+levelS);
				} else {
//					LOG.debug(logClass+": "+level);
					levelByClass.put(logClass, level);
					Logger.getLogger(logClass).setLevel(level);
				}
			}
		}
	}

	@Override
    public Void call() throws Exception {
        return null;
    }

    /** subclass this if you want to process CTree and CProject differently
     * 
     */
	protected boolean runGenerics() {
        return true;
	}

	/** validates the infput formats.
	 * Currently NOOP
	 * 
	 */
	protected void validateRawFormats() {
	}

	/** creates cProject from cProjectDirectory.
	 * checks it exists
	 * 
	 */
	protected void validateCProject() {
		if (cProjectDirectory != null) {
			File cProjectDir = new File(cProjectDirectory);
			if (!cProjectDir.exists() || !cProjectDir.isDirectory()) {
				throw new RuntimeException("cProject must be existing directory: "+cProjectDirectory);
			}
			cProject = new CProject(cProjectDir);
			cTreeList = generateCTreeList();
    	}
	}

	private CTreeList generateCTreeList() {
		cTreeList = new CTreeList();
		if (cProject != null) {
			List<String> includeTreeList = includeTrees == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(includeTrees));
			List<String> excludeTreeList = excludeTrees == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(excludeTrees));
			CTreeList pList = cProject == null ? new CTreeList() : cProject.getOrCreateCTreeList();
			for (CTree ct : pList) {
				String name = ct.getName();
				if (includeTreeList.size() > 0) {
					if (includeTreeList.contains(name)) {
						cTreeList.add(ct);
					}
				} else if (excludeTreeList.size() > 0) {
					if (!excludeTreeList.contains(name)) {
						cTreeList.add(ct);
					}
				} else {
					cTreeList.add(ct);
				}
			}
		} else if (cTree != null) {
			cTreeList.add(cTree);
		}
		return cTreeList;
	}

	/** creates cTree from cTreeDirectory.
	 * checks it exists
	 * 
	 */
	protected void validateCTree() {
		if (cTreeDirectory != null) {
			File cTreeDir = new File(cTreeDirectory);
			if (!cTreeDir.exists() || !cTreeDir.isDirectory()) {
				throw new RuntimeException("cTree must be existing directory: "+cTreeDirectory);
			}
			cTree = new CTree(cTreeDir);
			cTreeList = new CTreeList();
			cTreeList.add(cTree);
    	}
	}

	/** prints generic values from abstract superclass.
	 * at present cproject, ctree and filetypes
	 * 
	 */
	private void printGenericValues() {
        System.out.println("cproject            " + (cProject == null ? "" : cProject.getDirectory().getAbsolutePath()));
        System.out.println("ctree               " + (cTree == null ? "" : cTree.getDirectory().getAbsolutePath()));
        System.out.println("file types          " + Util.toStringList(rawFileFormats));
        System.out.println("cTreeList           " + cTreeList);
        System.out.println("basename            " + userBasename);
        System.out.println("include             " + includeTrees);
        System.out.println("exclude             " + excludeTrees);
        System.out.println("verbose             " + verbosity.length);
	}

	public void setCProject(CProject cProject) {
		this.cProject = cProject;
	}

	public void setCTree(CTree cTree) {
		this.cTree = cTree;
	}

	public CTree getCTree() {
		return cTree;
	}

	public void setCProjectOutputDir(File dir) {
		this.cProjectOutputDir = dir;
	}

	public File getCProjectOutputDir() {
		return cProjectOutputDir;
	}

	public void setCTreeOutputDir(File outputDir) {
		cTreeOutputDir = outputDir;
	}

	public File getCTreeOutputDir() {
		return cTreeOutputDir;
	}

	public CProject getCProject() {
		return cProject;
		
	}

	protected void printGenericHeader() {
		System.out.println();
		System.out.println("Generic values ("+this.getClass().getSimpleName()+")");
		System.out.println("================================");
	}

	protected void printSpecificHeader() {
		System.out.println();
		System.out.println("Specific values ("+this.getClass().getSimpleName()+")");
		System.out.println("================================");
	}

	protected void argument(Level level, String message) {
		combineLevel(level);
		if (level.isGreaterOrEqual(Level.WARN)) {
			System.err.println(this.getClass().getSimpleName()+": "+level + ": "+message);
		}
	}

	private void combineLevel(Level level) {
		if (level == null) {
			LOG.warn("null level");
		} else if (this.level== null) {
			this.level = level;
		} else if (level.isGreaterOrEqual(this.level)) {
			this.level = level;
		}
	}
	
	public Level getVerbosity() {
		if (verbosity.length == 0) {
			LOG.error("BUG?? in verbosity");
			return null;
		} else if (verbosity.length == 1) {
			 return verbosity[0] ? Level.INFO : Level.WARN; 
		} else if (verbosity.length == 2) {
			 return Level.DEBUG; 
		} else if (verbosity.length == 3) {
			 return Level.TRACE; 
		}
		return Level.ERROR;
		
	}

	/** creates toplevel ContentMine directory in which all dictionaries and other tools
	 * will be stored. By default this is "ContentMine" under the users home directory.
	 * It is probably not a good idea to store actual projects here, but we will eveolve the usage.
	 * 
	 * @return null if cannot create directory
	 */
	protected File getOrCreateExistingContentMineDir() {
		if (contentMineDir == null) {
			// null means cannot be created
		} else if (contentMineDir.exists()) {
			if (!contentMineDir.isDirectory()) {
				LOG.error(contentMineDir + " must be a directory");
				contentMineDir = null;
			}
		} else {
			LOG.info("Creating "+CONTENT_MINE_HOME+" directory: "+contentMineDir);
			try {
				contentMineDir.mkdirs();
			} catch (Exception e) {
				LOG.error("Cannot create "+contentMineDir);
				contentMineDir = null;
			}
		}
		return contentMineDir;
	}

	protected boolean processTrees() {
		boolean processed = cTreeList != null && cTreeList.size() > 0;
		if (cTreeList != null) {
			for (CTree cTree : cTreeList) {
				processTree(cTree);
			}
		}
		return processed;
	}
	
	protected void processTree(CTree cTree) {
		LOG.warn("Overide this");
	}

}
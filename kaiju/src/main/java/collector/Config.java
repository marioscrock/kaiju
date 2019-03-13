package collector;

import com.beust.jcommander.Parameter;

public class Config {

	@Parameter(names={"--mode", "-m"})
    public String mode;
    @Parameter(names={"--rtime", "-rt"})
    public String retentionTime = "2min";
    @Parameter(names={"--parse"})
    public boolean parse = true;
    
}

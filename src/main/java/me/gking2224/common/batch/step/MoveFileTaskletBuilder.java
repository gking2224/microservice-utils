package me.gking2224.common.batch.step;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.batch.repeat.RepeatStatus.FINISHED;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import me.gking2224.common.ObjectOrSupplier;

public class MoveFileTaskletBuilder {

    private static Logger logger = LoggerFactory.getLogger(MoveFileTaskletBuilder.class);
    
    private static final String SUCCESS_MSG = "Successfully moved file {} to {}";
    
    private ObjectOrSupplier<File> file;
    private String suffix;
    private File toDir;
    private String newName;
    private boolean replaceExisting = false;
    
    public MoveFileTaskletBuilder addSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }
    
    public MoveFileTaskletBuilder file(File file) {
        this.file = new ObjectOrSupplier<File>(file);
        return this;
    }
    
    public MoveFileTaskletBuilder file(Supplier<File> file) {
        this.file = new ObjectOrSupplier<File>(file);
        return this;
    }
    
    public MoveFileTaskletBuilder newName(String newName) {
        this.newName = newName;
        return this;
    }
    
    public MoveFileTaskletBuilder toDir(File toDir) {
        this.toDir = toDir;
        return this;
    }
    
    public MoveFileTaskletBuilder replaceExisting() {
        this.replaceExisting = true;
        return this;
    }
    
    public Tasklet build() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                validate();
                
                File newFile = new File(toDir, buildNewName());
                File f = file.get();
                Path from = FileSystems.getDefault().getPath(f.getAbsolutePath());
                Path to = FileSystems.getDefault().getPath(newFile.getAbsolutePath());
                List<CopyOption> options = new ArrayList<CopyOption>();
                if (replaceExisting) options.add(REPLACE_EXISTING);
                Files.move(from, to, options.toArray(new CopyOption[options.size()]));
                logger.debug(SUCCESS_MSG, f.getAbsolutePath(), newFile.getAbsolutePath());
                return FINISHED;
                
            }

            private void validate() {
                File f = file.get();
                if (!f.isFile()) throw new TaskletFailedException(String.format("File %s is not a file", f.getAbsolutePath()));
                if (newName == null && suffix == null && toDir != null && toDir.getAbsolutePath().equals(f.getAbsoluteFile()))
                    throw new TaskletFailedException("Source and destination files are equal");
                if (newName != null && suffix != null) throw new TaskletFailedException("Cannot specify newName as well as suffix");
                if (newName == null && suffix == null && toDir == null) throw new TaskletFailedException("At least one of newName, suffix or toDir must be specified");
                if (toDir == null) toDir = f.getParentFile();
            }
        };
    }

    protected String buildNewName() {
        if (newName != null) return newName;
        else if (suffix != null) return String.format("%s.%s", file.get().getName(), suffix);
        else return file.get().getName();
    }
    
}
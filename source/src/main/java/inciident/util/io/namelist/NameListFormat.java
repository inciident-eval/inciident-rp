
package inciident.util.io.namelist;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import inciident.util.data.Result;
import inciident.util.io.InputMapper;
import inciident.util.io.format.Format;


public class NameListFormat implements Format<List<NameListFormat.NameEntry>> {

    public static class NameEntry {
        private final String name;
        private final int id;

        public NameEntry(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getID() {
            return id;
        }
    }

    public static final String ID = NameListFormat.class.getCanonicalName();

    private static final String COMMENT = "#";
    private static final String STOP_MARK = "###";

    @Override
    public Result<List<NameEntry>> parse(InputMapper inputMapper) {
        final List<String> lines = inputMapper.get().readLines();
        return parse(lines, new ArrayList<>(lines.size()));
    }

    @Override
    public Result<List<NameEntry>> parse(InputMapper inputMapper, Supplier<List<NameEntry>> supplier) {
        final List<String> lines = inputMapper.get().readLines();
        return parse(lines, supplier.get());
    }

    private Result<List<NameEntry>> parse(final List<String> lines, final List<NameEntry> entries) {
        int lineNumber = 0;
        boolean pause = false;
        for (final String modelName : lines) {
            lineNumber++;
            if (!modelName.trim().isEmpty()) {
                if (!modelName.startsWith("\t")) {
                    if (modelName.startsWith(COMMENT)) {
                        if (modelName.equals(STOP_MARK)) {
                            pause = !pause;
                        }
                    } else if (!pause) {
                        entries.add(new NameEntry(modelName, lineNumber));
                    }
                }
            }
        }
        return Result.of(entries);
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String getFileExtension() {
        return "list";
    }

    @Override
    public String getName() {
        return "Name List";
    }

    @Override
    public boolean supportsParse() {
        return true;
    }
}

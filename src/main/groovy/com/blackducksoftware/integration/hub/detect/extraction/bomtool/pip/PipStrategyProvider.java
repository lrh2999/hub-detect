package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.PipExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.PipInspectorRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.PythonExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class PipStrategyProvider extends StrategyProvider {

    private final String SETUP_FILE_NAME = "setup.py";

    @Autowired
    DetectConfiguration detectConfiguration;

    @SuppressWarnings("rawtypes")
    @Override
    public List<Strategy> createStrategies() {

        final Strategy setupPiStrategy = newStrategyBuilder(PipInspectorContext.class, PipInspectorExtractor.class)
                .needsBomTool(BomToolType.PIP).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsFile(SETUP_FILE_NAME).as((context, file) -> context.setupFile = file)
                .demands(new PythonExecutableRequirement()).as((context, file) -> context.pythonExe = file)
                .demands(new PipExecutableRequirement()).noop()
                .demands(new PipInspectorRequirement()).as((context, file) -> context.pipInspector = file)
                .build();

        //TODO: Max depth: 1 (can only apply at root), will apply if requirements file is provided
        final Strategy requirementStrategy = newStrategyBuilder(PipInspectorContext.class, PipInspectorExtractor.class)
                .needsBomTool(BomToolType.PIP).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsString(detectConfiguration.getRequirementsFilePath()).as((context, file) -> context.requirementFilePath = file)
                .demands(new PythonExecutableRequirement()).as((context, file) -> context.pythonExe = file)
                .demands(new PipExecutableRequirement()).noop()
                .demands(new PipInspectorRequirement()).as((context, file) -> context.pipInspector = file)
                .build();

        return Arrays.asList(setupPiStrategy);

    }

}

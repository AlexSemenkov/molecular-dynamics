package com.asemenkov.gromacs.frame;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.asemenkov.gromacs.particles.GmxAtom;
import com.asemenkov.gromacs.particles.GmxResidue;

/**
 * @author asemenkov
 * @since Apr 17, 2018
 */
public class GmxFrameStructure {

    private final float[] box;
    private final Class<? extends GmxAtom>[] atomsSequence;
    private final String[] atomAbbreviationsSequence;
    private final Map<Class<? extends GmxResidue>, int[]> residueIndexesMap;
    private final Map<Integer, int[]> residueAtomsMap;
    private String description;

    @SuppressWarnings("unchecked")
    public GmxFrameStructure(int size) {
        this.box = new float[3];
        this.atomAbbreviationsSequence = new String[size];
        this.atomsSequence = (Class<? extends GmxAtom>[]) new Class[size];
        this.residueIndexesMap = new ConcurrentHashMap<>();
        this.residueAtomsMap = new ConcurrentHashMap<>();
    }

    // ======== GETTERS ========

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public Integer getAtomsNum() {
        return atomsSequence.length;
    }

    public Integer getResiduesNum() {
        return residueIndexesMap.values().stream().mapToInt(value -> value.length).sum();
    }

    public float[] getBox() {
        return Arrays.copyOf(box, 3);
    }

    void setBox(float[] box) {
        System.arraycopy(box, 0, this.box, 0, 3);
    }

    public Class<? extends GmxAtom>[] getAtomsSequence() {
        return atomsSequence;
    }

    public String[] getAtomAbbreviationsSequence() {
        return atomAbbreviationsSequence;
    }

    // ======== SETTERS ========

    public Map<Class<? extends GmxResidue>, int[]> getResidueIndexesMap() {
        return residueIndexesMap;
    }

    public Map<Integer, int[]> getResidueAtomsMap() {
        return residueAtomsMap;
    }

    void setAtomAbbreviation(int index, String atomAbbreviation) {
        atomAbbreviationsSequence[index] = atomAbbreviation;
    }

    void setAtomsClass(int index, Class<? extends GmxAtom> atomClass) {
        atomsSequence[index] = atomClass;
    }

    void setResidueIndexes(Class<? extends GmxResidue> residueClass, int[] indexes) {
        residueIndexesMap.put(residueClass, indexes);
    }

    void setResidueAtoms(int residueIndex, int[] indexes) {
        residueAtomsMap.put(residueIndex, indexes);
    }

}
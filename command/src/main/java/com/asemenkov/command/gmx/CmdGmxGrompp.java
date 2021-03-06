package com.asemenkov.command.gmx;

import com.asemenkov.utils.io.FileUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * gmx grompp (the gromacs preprocessor) reads a molecular topology file, checks the validity of the file,
 * expands the topology from a molecular description to an atomic description.
 * The topology file contains information about molecule types and the number of molecules,
 * the preprocessor copies each molecule as needed. There is no limitation on the number of molecule types.
 * Bonds and bond-angles can be converted into constraints, separately for hydrogens and heavy atoms.
 * Then a coordinate file is read and velocities can be generated from a Maxwellian distribution if requested.
 * gmx grompp also reads parameters for gmx mdrun (eg. number of MD steps, time step, cut-off),
 * and others such as NEMD parameters, which are corrected so that the net acceleration is zero.
 * Eventually a binary file is produced that can serve as the sole input file for the MD program.
 * <p>
 *
 * @author asemenkov
 * @see <a href="http://manual.gromacs.org/documentation/2018/onlinehelp/gmx-grompp.html">GROMACS manual</a>
 * @since May 02, 2019
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class CmdGmxGrompp extends CmdGmxAbstractCommand<CmdGmxGrompp> {

    public CmdGmxGrompp() {
        super("grompp");
    }

    // ======== Options to specify input files ========

    /**
     * -f [<.mdp>] (grompp.mdp)
     * grompp input file with MD parameters
     */
    public CmdGmxGrompp withMdInputFile(Path f) {
        FileUtils.verifyFileInDirExists(f, simulationPath);
        FileUtils.verifyExtension(f, ".mdp");
        commandsMap.put("-f", f.getFileName().toString());
        return this;
    }

    /**
     * -c [<.gro/.g96/…>] (conf.gro)
     * Structure file: gro g96 pdb brk ent esp tpr
     */
    public CmdGmxGrompp withStructureInputFile(Path c) {
        FileUtils.verifyFileInDirExists(c, simulationPath);
        FileUtils.verifyExtension(c, ".gro", ".g96", ".pdb", ".brk", ".ent", ".esp", ".tpr");
        commandsMap.put("-c", c.getFileName().toString());
        return this;
    }

    /**
     * -r [<.gro/.g96/…>] (restraint.gro) (Optional)
     * Structure file: gro g96 pdb brk ent esp tpr
     */
    public CmdGmxGrompp withRestraintInputFile(Path r) {
        FileUtils.verifyFileInDirExists(r, simulationPath);
        FileUtils.verifyExtension(r, ".gro", ".g96", ".pdb", ".brk", ".ent", ".esp", ".tpr");
        commandsMap.put("-r", r.getFileName().toString());
        return this;
    }

    /**
     * -rb [<.gro/.g96/…>] (restraint.gro) (Optional)
     * Structure file: gro g96 pdb brk ent esp tpr
     */
    public CmdGmxGrompp withRestraintBInputFile(Path rb) {
        FileUtils.verifyFileInDirExists(rb, simulationPath);
        FileUtils.verifyExtension(rb, ".gro", ".g96", ".pdb", ".brk", ".ent", ".esp", ".tpr");
        commandsMap.put("-rb", rb.getFileName().toString());
        return this;
    }

    /**
     * -n [<.ndx>] (index.ndx) (Optional)
     * Index file
     */
    public CmdGmxGrompp withIndexInputFile(Path n) {
        FileUtils.verifyFileInDirExists(n, simulationPath);
        FileUtils.verifyExtension(n, ".ndx");
        commandsMap.put("-n", n.getFileName().toString());
        return this;
    }

    /**
     * -p [<.top>] (topol.top)
     * Topology file
     */
    public CmdGmxGrompp withTopologyInputFile(Path p) {
        FileUtils.verifyFileInDirExists(p, simulationPath);
        FileUtils.verifyExtension(p, ".top");
        commandsMap.put("-p", p.getFileName().toString());
        return this;
    }

    /**
     * -t [<.trr/.cpt/…>] (traj.trr) (Optional)
     * Full precision trajectory: trr cpt tng
     */
    public CmdGmxGrompp withTrajectoryInputFile(Path t) {
        FileUtils.verifyFileInDirExists(t, simulationPath);
        FileUtils.verifyExtension(t, ".trr", ".cpt", ".tng");
        commandsMap.put("-t", t.getFileName().toString());
        return this;
    }

    /**
     * -e [<.edr>] (ener.edr) (Optional)
     * Energy file
     */
    public CmdGmxGrompp withEnergyInputFile(Path e) {
        FileUtils.verifyFileInDirExists(e, simulationPath);
        FileUtils.verifyExtension(e, ".edr");
        commandsMap.put("-e", e.getFileName().toString());
        return this;
    }

    // ======== Options to specify input/output files ========

    /**
     * -ref [<.trr/.cpt/…>] (rotref.trr) (Optional)
     * Full precision trajectory: trr cpt tng
     */
    public CmdGmxGrompp withReferenceFile(Path ref) {
        FileUtils.verifyFileInDirExists(ref, simulationPath);
        FileUtils.verifyExtension(ref, ".trr", ".cpt", ".tng");
        commandsMap.put("-ref", ref.getFileName().toString());
        return this;
    }

    // ======== Options to specify output files ========

    /**
     * -po [<.mdp>] (mdout.mdp)
     * grompp output file with MD parameters
     */
    public CmdGmxGrompp withMdOutputFile(Path po) {
        FileUtils.verifyExtension(po, ".mdp");
        commandsMap.put("-po", po.getFileName().toString());
        return this;
    }

    /**
     * -pp [<.top>] (processed.top) (Optional)
     * Topology file
     */
    public CmdGmxGrompp withTopologyOutputFile(Path pp) {
        FileUtils.verifyExtension(pp, ".top");
        commandsMap.put("-pp", pp.getFileName().toString());
        return this;
    }

    /**
     * -o [<.tpr>] (topol.tpr)
     * Portable xdr run output file
     */
    public CmdGmxGrompp withRunOutputFile(Path o) {
        FileUtils.verifyExtension(o, ".tpr");
        commandsMap.put("-o", o.getFileName().toString());
        return this;
    }

    /**
     * -imd [<.gro>] (imdgroup.gro) (Optional)
     * Coordinate file in Gromos-87 format
     */
    public CmdGmxGrompp withGromos87CoordinateOutputFile(Path imd) {
        FileUtils.verifyExtension(imd, ".gro");
        commandsMap.put("-imd", imd.getFileName().toString());
        return this;
    }

    // ======== Other options ========

    /**
     * -[no]v (no)
     * Be loud and noisy
     */
    public CmdGmxGrompp withLoudAndNoisy(Boolean v) {
        commandsMap.put(v ? "-v" : "-nov", "");
        return this;
    }

    /**
     * -time <real> (-1)
     * Take frame at or first after this time.
     */
    public CmdGmxGrompp withTime(Double time) {
        commandsMap.put("-time", time.toString());
        return this;
    }

    /**
     * -[no]rmvsbds (yes)
     * Remove constant bonded interactions with virtual sites
     */
    public CmdGmxGrompp withVirtualSitesRemoval(Boolean rmvsbds) {
        commandsMap.put(rmvsbds ? "-rmvsbds" : "-normvsbds", "");
        return this;
    }

    /**
     * -maxwarn <int> (0)
     * Number of allowed warnings during input processing. Not for normal use and may generate unstable systems
     */
    public CmdGmxGrompp withMaximumWarnings(Integer maxwarn) {
        commandsMap.put("-maxwarn", maxwarn.toString());
        return this;
    }

    /**
     * -[no]zero (no)
     * Set parameters for bonded interactions without defaults to zero instead of generating an error
     */
    public CmdGmxGrompp withZero(Boolean zero) {
        commandsMap.put(zero ? "-zero" : "-nozero", "");
        return this;
    }

    /**
     * -[no]renum (yes)
     * Renumber atom types and minimize number of atom types
     */
    public CmdGmxGrompp withRenumbering(Boolean renum) {
        commandsMap.put(renum ? "-renum" : "-norenum", "");
        return this;
    }

}

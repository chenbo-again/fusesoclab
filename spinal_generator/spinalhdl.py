#!/usr/bin/env python3
from fusesoc.capi2.generator import Generator
import os
import shutil
import shlex
import subprocess
import tempfile
import json
from fusesoc import utils


class SpinalHDLGenerator(Generator):
    def run(self):
        env = self.config.get('env', None)
        buildtool = self.config.get('buildtool', "sbt")
        target_directory = self.config.get('target_directory', "generated")
        spinal_parameter = json.dumps(self.config.get('spinal_parameter', None), indent=None,  separators=(',', ':'))
        print(env, buildtool, target_directory)

        copy_core = self.config.get('copy_core', False)
        if copy_core:
            tmp_dir = os.path.join(tempfile.mkdtemp(), 'core')
            shutil.copytree(self.files_root, tmp_dir,
                            ignore=shutil.ignore_patterns('out', 'generated'))

        cwd = tmp_dir if copy_core else self.files_root

        files = self.config['output'].get('files', [])
        parameters = self.config['output'].get('parameters', {})

        # Find build tool, first in root dir, then ./scripts dir then in path
        buildcmd = []
        if self._is_exe(os.path.join(cwd, buildtool)):
            buildcmd.append(os.path.join(cwd, buildtool))
        elif self._is_exe(os.path.join(cwd, "scripts", buildtool)):
            buildcmd.append(os.path.join(cwd, "scripts", buildtool))
        elif shutil.which(buildtool) is not None:
            buildcmd.append(shutil.which(buildtool))
        else:
            print("Build tool " + buildtool + " not found")
            exit(1)
        print("Using build tool from: " + buildcmd[0])

        # Define command and arguments based on build tool
        if buildtool == "sbt":
            args = '"run'+ ' ' + "--spinal_parameter" + " " + spinal_parameter + " " + "--target_directory" + " " + target_directory +'"'
        else:
            raise
        print("############", args)
        


        args = shlex.split(args)

        # Concatenate environment variables from system + user defined
        d = os.environ
        if env:
            d.update(env)

        # Call build tool
        cmd = buildcmd + args
        if os.getenv('EDALIZE_LAUNCHER'):
            cmd = [os.getenv('EDALIZE_LAUNCHER')] + cmd

        print("Working dir:", cwd)
        print("Running:", " ".join(cmd))
        rc = subprocess.call(cmd, env=d, cwd=cwd)

        if rc:
            exit(1)
        if cwd:
            filenames = []
            for f in files:
                for k in f:
                    filenames.append(k)

            for f in filenames:
                d = os.path.dirname(f)
                if d and not os.path.exists(d):
                    os.makedirs(d)
                shutil.copy2(os.path.join(cwd, f), f)

        self.add_files(files)

        for k, v in parameters.items():
            self.add_parameter(k, v)

    def _is_exe(self, fpath):
        return os.path.isfile(fpath) and os.access(fpath, os.X_OK)


g = SpinalHDLGenerator()
g.run()
g.write()
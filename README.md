Abstract IDE

Welcome to **Abstract**! This is a Universal Coding Editor which will work with ALL text based languages. It does this by formatting your files into bubbles, and lets
you organize it with many different settings. It then uses AI to create descriptions for all parts of the project, or code from descriptions. In the future,
hopefully it will be able to parse one function at a time, and translate it to any other language of your choice.

This is a public, source-available coding project developed and maintained by Lanse.  
You're free to view and learn from the code, and you're welcome to contribute — but **you may not use it in any commercial or redistributed software**.

## License

This project is licensed under the **Business Source License 1.1 (BUSL-1.1)**.  
- **You may:** View the source code, learn from it, and contribute via pull requests.
- **You may not:** Use this code in commercial products, services, or redistribute it in whole or in part without express permission.
- **There is no Change Date** — this project will remain under the BUSL indefinitely.

See the full [LICENSE](./LICENSE) file for details.

## Dependencies

 - Java 17.+
 - Gradle 8.+

## Build/Run Instructions

BASH:
```
# to build
git clone https://github.com/Lanse0123/Abstract.git
cd Abstract
make build
# to run (while still in the cloned repo)
make run
```

## Notes for Releasing
``./gradlew assembleShadowDist`` creates .tar and .zip files in build/distributions. These files have a bin directory, with ``Abstract`` and ``Abstract.bat`` launchers for the jar files that are also included in the compressed file. For testing, there is a ``build/install/Abstract`` folder which contains the uncompressed contents of these files.

## Troubleshooting

If you are using linux with a non-reparenting wm and the output from running Abstract is a completely blank screen, run the following:
``export _JAVA_AWT_WM_NONREPARENTING=1``

## Contributing

Contributions are welcome!  
If you'd like to improve the project, fix bugs, or add features:

1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Submit a pull request.

By submitting a pull request, you agree to license your contribution under the same **BUSL-1.1** terms as the rest of the project.
Unlike the regular usage of BUSL-1.1, I do not set it to change in 4 years, so it will stay the same forever, unless I manually change it.
I just dont want people to steal the project and make my life difficult D:

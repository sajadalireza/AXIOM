# Vendored Fonts

AXIOM vendors the five font binaries below so Android builds do not depend on
network availability. The files are unmodified copies of official upstream
artifacts and are licensed under the SIL Open Font License 1.1.

## Asset Inventory

| App resource | Upstream artifact and immutable pin | SHA-256 | License |
|---|---|---|---|
| `app/src/main/res/font/outfit_variable.ttf` | [Google Fonts Outfit variable](https://github.com/google/fonts/blob/5f246070882b903ed95a911dba83d9d4a6836152/ofl/outfit/Outfit%5Bwght%5D.ttf) at commit `5f246070882b903ed95a911dba83d9d4a6836152` | `fc7287273e66929776e2ba54f144fe699080bec29f61bf649d70d871468aeade` | [Outfit OFL 1.1](outfit/OFL.txt) |
| `app/src/main/res/font/fraunces_variable.ttf` | [Google Fonts Fraunces variable](https://github.com/google/fonts/blob/ac502d8eff76ef4d9477cdcc8ef7d0c84fde5372/ofl/fraunces/Fraunces%5BSOFT%2CWONK%2Copsz%2Cwght%5D.ttf) at commit `ac502d8eff76ef4d9477cdcc8ef7d0c84fde5372` | `177ff6c0f14e5550a3c624247cd1189611d4eb65d000b14944c63d967958abbb` | [Fraunces OFL 1.1](fraunces/OFL.txt) |
| `app/src/main/res/font/fraunces_italic_variable.ttf` | [Google Fonts Fraunces italic variable](https://github.com/google/fonts/blob/ac502d8eff76ef4d9477cdcc8ef7d0c84fde5372/ofl/fraunces/Fraunces-Italic%5BSOFT%2CWONK%2Copsz%2Cwght%5D.ttf) at commit `ac502d8eff76ef4d9477cdcc8ef7d0c84fde5372` | `b24448c43702fac4ee856781d461a0dfba8d8e594b6e8e190234b75fed2c0e01` | [Fraunces OFL 1.1](fraunces/OFL.txt) |
| `app/src/main/res/font/fira_code_regular.ttf` | [Fira Code Regular](https://cdn.jsdelivr.net/npm/firacode@6.2.0/distr/ttf/FiraCode-Regular.ttf), package `firacode@6.2.0`, upstream tag commit `eee6db993696aba61ff4eef03698e2987d79910c` | `5992ab9640e2df491b2f609467b1de60e8bc39b2c28db184342a0592d98f6117` | [Fira Code OFL 1.1](fira-code/OFL.txt) |
| `app/src/main/res/font/fira_code_medium.ttf` | [Fira Code Medium](https://cdn.jsdelivr.net/npm/firacode@6.2.0/distr/ttf/FiraCode-Medium.ttf), package `firacode@6.2.0`, upstream tag commit `eee6db993696aba61ff4eef03698e2987d79910c` | `97091f90623661fb4f7979c10d188f30f4806d8ce326b0bc8d1acc79dcc20d8f` | [Fira Code OFL 1.1](fira-code/OFL.txt) |

License file checksums:

```text
e2a6bbb2589b85bac1690ec8f220b22a9c3af9bfda56df6391f7ab491c7a039f  outfit/OFL.txt
6732d6cc72c5d09292ff754dc1f39d9ea14918f74e87a17afa3f00a5120c3d48  fraunces/OFL.txt
1d41e10031ab125302780a05ec4c91d218e47db0c7e37cf315cce5e608cdc25c  fira-code/OFL.txt
```

## Build Integrity

`./gradlew verifyVendoredFonts` checks every required binary against the
SHA-256 values above. Android `preBuild` depends on that local verification.
A missing or changed asset fails the build with the affected path, expected
checksum, and actual checksum. The build never downloads fonts and never writes
fallback bytes into `app/src`.

`Inter` and `JetBrainsMono` remain compatibility aliases for Outfit and Fira
Code in the existing Compose theme. This packet does not change typography or
select new brand fonts.

When intentionally updating a font, replace the binary from an immutable
official source and update its checksum, upstream pin, and license evidence in
the same change.

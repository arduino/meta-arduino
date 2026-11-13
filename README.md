# Arduino Yocto Project

This repository contains the Yocto layers designed to support Arduino microprocessor-based boards.

Main layer maintainer: Massimo Pennazio maxipenna@libero.it

## Layers Overview

### 1. `meta-arduino-common`

This layer contains common components shared across Arduino platforms:
- Android tools (ADB support)
- Arduino CLI tooling (arduino-cli, arduino-app-cli, arduino-router)
- Avahi and NetworkManager configurations
- Legacy: m4-proxy (old arduino-router)
- Common image recipes (console, weston, multimedia, container, full, OTBR), see [Image Recipes](DEVELOPERS.md#image-recipes)
- USB gadget configurations (libusbgx-config)

### 2. `meta-arduino-nxp`

This layer supports boards using NXP processors. It includes:
- Spl/U-Boot & Linux Kernel machines and configurations
- Device tree overlays
- Custom recipes specific to NXP platforms

**Warning**: Only the `scarthgap` branch is supported at the moment.

**Dependencies**: `meta-arduino-common`

### 3. `meta-arduino-qcom`

This layer supports Qualcomm-based boards (such as Imola / Arduino UNO Q and Monza / VentUNO Q). It includes BSP extensions, machine configurations, device trees, and custom recipes for Qualcomm platforms.

**Dependencies**: `meta-arduino-common`

### 4. `meta-arduino-lmp`

This adds compatibility to [Linux Micro Platform (LMP) distribution from Foundries.io](https://github.com/foundriesio/lmp-manifest).

**Warning**: Only the `scarthgap` branch is supported at the moment.

**Dependencies**: `meta-arduino-common`

## Getting Started

See [DEVELOPERS.md](DEVELOPERS.md) for instructions on setting up the build environment, building NXP, QCom and LmP images, and adding custom devicetree overlays or kernel modules.

## Supported Hardware

See [HARDWARE.md](HARDWARE.md) for details on supported machines, carrier boards, camera modules, display panels, wireless modules, and secure elements.

## Projects Using meta-arduino

- [meta-pika-spark](https://github.com/pika-spark/meta-pika-spark): Matchbox-sized high-performance computing for mobile robots.

## Contributing

Contributions are welcome! Please submit pull requests or open issues for any bugs or feature requests.

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

#!/usr/bin/env python3
import argparse
import pathlib
import re


def replace_define(contents: str, key: str, value: str) -> str:
    pattern = rf'(#define\s+{re.escape(key)}\s+)(.+)'
    return re.sub(pattern, rf'\1{value}', contents)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--header", required=True)
    parser.add_argument("--semver", required=True)
    parser.add_argument("--version-code", required=True, type=int)
    parser.add_argument("--channel", required=True)
    args = parser.parse_args()

    header_path = pathlib.Path(args.header)
    contents = header_path.read_text(encoding="utf-8")
    contents = replace_define(contents, "FW_SEMVER", f'"{args.semver}"')
    contents = replace_define(contents, "FW_VERSION_CODE", str(args.version_code))
    contents = replace_define(contents, "FW_CHANNEL", f'"{args.channel}"')
    header_path.write_text(contents, encoding="utf-8")


if __name__ == "__main__":
    main()


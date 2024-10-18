import typescriptEslint from "@typescript-eslint/eslint-plugin";
import tsParser from "@typescript-eslint/parser";
import path from "node:path";
import { fileURLToPath } from "node:url";
import js from "@eslint/js";
import { FlatCompat } from "@eslint/eslintrc";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const compat = new FlatCompat({
    baseDirectory: __dirname,
    recommendedConfig: js.configs.recommended,
    allConfig: js.configs.all,
});

export default [{
    ignores: ["**/node_modules", "**/dist", "**/coverage", "**/webpack.config.js"],
}, ...compat.extends("plugin:@typescript-eslint/recommended"), {
    plugins: {
        "@typescript-eslint": typescriptEslint,
    },

    languageOptions: {
        parser: tsParser,
    },

    rules: {
        "brace-style": ["error", "stroustrup"],

        quotes: ["error", "double", {
            allowTemplateLiterals: true,
        }],

        indent: ["error", 4],

        "comma-dangle": ["error", {
            arrays: "never",
            objects: "always",
            imports: "never",
            exports: "never",
            functions: "never",
        }],
    },
}];

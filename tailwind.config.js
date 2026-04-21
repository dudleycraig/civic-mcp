module.exports = {
  important: true,
  content: [
    "./resources/public/**/*.{html}",
    "./src/core/ui/**/*.{cljs}",
    "./src/test/ui/**/*.{cljs}",
  ],
  theme: {
    extend: {}
  },
  plugins: [
    require("@tailwindcss/typography"),
  ],
  daisyui: {
    themes: [
      "light", "dark", "retro", "abyss", "bumblebee", "black", "wireframe", "caramellatte", "coffee", "autumn"
    ]
  }
};

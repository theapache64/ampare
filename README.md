![](cover.jpeg)

# 📈 ampare

<a href="https://twitter.com/theapache64" target="_blank">
<img alt="Twitter: theapache64" src="https://img.shields.io/twitter/follow/theapache64.svg?style=social" />
</a>

A web-based analytics event comparison tool that helps identify differences between two event data sets. Perfect for debugging analytics implementations and validating event changes.

🔗 **Live Tool**: [a64.in/ampare](https://a64.in/ampare)

🧩 **Chrome Extension**: [Install from Chrome Web Store](https://chrome.google.com/webstore/detail/ampare/loipdahhaleeggfcjmmkbhboagbkljgn) - Adds a "Copy to Clipboard" button to Amplitude event details for easy data extraction.

## Features & Usage

- **Side-by-side comparison** with color-coded differences: 🟡 Modified, 🟢 Added, 🔴 Removed, ⚪ Unchanged
- **Statistics overview** showing counts for each category
- **Responsive interface** for mobile and desktop
- **Simple copy-paste workflow** - paste your event data and compare

### How to Use
1. Paste your first event data → Paste your second event data → Click "Compare Events"
2. Review the color-coded differences and statistics

### Supported Format
```
propertyKey1
propertyValue1
propertyKey2
propertyValue2
```

## ✍️ Author

👤 **theapache64** - [Twitter](https://twitter.com/theapache64) - [Email](mailto:theapache64@gmail.com)

## 🤝 Contributing

Contributions are welcome! Please open an issue first to discuss what you would like to change, then follow the standard fork-branch-PR workflow.

## 🚀 Chrome Extension

The extension adds a "Copy to Clipboard" button to Amplitude's event details view, making it easy to extract event data for comparison.

### Publishing

This project uses GitHub Actions to automatically publish the Chrome extension when a new version tag is pushed. Update the version in `chrome/src/jsMain/resources/manifest.json`, commit, tag (e.g., `v1.0.2`), and push.

## ❤ Support & License

If this project helped you, consider giving it a ⭐️ or <a href="https://www.buymeacoffee.com/theapache64" target="_blank">buying me a coffee</a>.

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

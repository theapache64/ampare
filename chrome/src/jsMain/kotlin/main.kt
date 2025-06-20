import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.get


fun main() {
    println("Ampare - Loaded successfully! 🎉")
    val observer = MutationObserver { mutations, _ ->
        // Check if  `<div data-testid="timeline-event-details">` is created
        mutations.forEach { mutation ->
            mutation.addedNodes.length.let { addedNodesCount ->
                if (addedNodesCount > 0) {
                    for (index in 0 until addedNodesCount) {
                        val eventDetails = mutation.addedNodes[index]
                        if (eventDetails is org.w3c.dom.Element && eventDetails.attributes["data-testid"]?.value == "timeline-event-details") {
                            println("QuickTag: :main: Event details div found")
                            // Create a button
                            val clipboardButton = document.createElement("button") as HTMLButtonElement
                            clipboardButton.textContent = "📋 Copy to Clipboard"

                            // Style clipboard button

                            // Set styles for the button to Amplitude blue color button
                            clipboardButton.style.apply {
                                backgroundColor = "#1e61f0" // Amplitude blue
                                color = "#FFFFFF" // White text
                                border = "none"
                                borderRadius = "4px"
                                padding = "8px 12px"
                                fontSize = "14px"
                                cursor = "pointer"
                                margin = "10px"
                            }

                            clipboardButton.onclick = {
                                println("QuickTag: :main: Copy to Clipboard button clicked")

                                // Check if 'See all events' button is clicked or not (data-testid="modal button")
                                var modalButton =
                                    eventDetails.querySelector("button[data-testid='modal button']") as HTMLButtonElement?
                                if (modalButton == null) {
                                    println("QuickTag: :main: No modal button found, checking for other button")
                                    modalButton =
                                        eventDetails.querySelector("button[data-testid='button']") as HTMLButtonElement?
                                    if (modalButton != null && modalButton.attributes["aria-labelledby"]?.value?.startsWith(
                                            "_see all other "
                                        ) == false
                                    ) {
                                        modalButton = null
                                    }
                                }
                                println("QuickTag: :main: Modal button found")
                                var delay = 0
                                if (modalButton?.textContent?.trim()?.startsWith("See all other ") == true) {
                                    // Click modal button
                                    modalButton.click()
                                    delay = 500
                                }

                                // Change button text to "Copied!" for 2 seconds
                                clipboardButton.textContent = "✅ Copied!"
                                window.setTimeout({
                                    clipboardButton.textContent = "📋 Copy to Clipboard"
                                }, 2000)

                                window.setTimeout({
                                    println("QuickTag: :main: Copying to clipboard...")
                                    // Copy data to clipboard
                                    val textContent = eventDetails.innerHTML
                                    val textArea = document.createElement("textarea") as HTMLTextAreaElement
                                    textArea.value = textContent;
                                    document.body?.appendChild(textArea);
                                    textArea.select();
                                    document.execCommand("copy");
                                    document.body?.removeChild(textArea);
                                    println("QuickTag: :main: Copied to clipboard successfully!")
                                }, delay)
                            }
                            // Append the button to the event details div
                            eventDetails.prepend(clipboardButton)

                        }
                    }
                }
            }
        }

    }

    observer.observe(document.body!!, MutationObserverInit(childList = true, subtree = true));
}

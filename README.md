# VoteX - A Secure and Versatile Voting Application

VoteX is a modern voting platform designed to accommodate diverse voting needs, from public polls to private voting and full-scale elections. The application is built with a focus on simplicity, security, and adaptability, making it suitable for both casual and formal use cases.

## Features

- **User Authentication:**
  Only authenticated users can access the app and participate in voting. Firebase Authentication ensures secure and reliable user management.

- **Public and Private Voting:**
  - Public voting is open to all authenticated users.
  - Private voting is restricted to invited participants based on predefined credentials.

- **Election Mode:**
  Structured specifically for elections, with candidates represented by `teamAlias`.

- **Credential Management:**
  Voters must enter specific credentials before participating in private or election-based voting. These credentials are securely stored in Firebase under `credential/voterCredential/(user UID)`.

- **Dynamic Options Display:**
  Voting options are dynamically fetched based on the voting type:
  - **Public/Private:** Retrieved from the `pilihan` attribute in Firebase.
  - **Election:** Retrieved from the `teamAlias` attribute in Firebase.

## Technical Stack

- **Frontend:** Built with a modular structure to ensure clean and maintainable code.
- **Backend:** Firebase Realtime Database for storing voting data and credentials.
- **Networking Logic:** Supports robust, modular voting logic with features like `submitVote` functions, error handling using `addOnSuccessListener` and `addOnFailureListener`, and clear workflows for vote submission.

## Security and Privacy

- Voter credentials are securely managed and cannot be tampered with post-submission.
- Sensitive information, such as voter identity and voting results, is securely handled to ensure data privacy.

## How to Contribute

We welcome contributions! If you would like to improve VoteX or report an issue, please submit a pull request or open an issue in this repository.

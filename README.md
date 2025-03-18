# spotifyplaylist-aigenerator-backend

A project integrating Stripe, Spotify, and OpenAI APIs to generate AI-curated playlists based on user prompts.

Overview

This project allows users to create personalized Spotify playlists by entering a natural language prompt. For example:

"Create a playlist with 20 songs that resemble Free Bird by Lynyrd Skynyrd."

The OpenAI API processes the prompt and generates a list of recommended songs. The Spotify API then searches for these songs, creates a new playlist, and adds the recommended tracks to it. Additionally, Stripe integration is included for potential monetization or premium features.

Features

AI-Powered Playlist Generation: OpenAI generates song recommendations based on user prompts.

Spotify Integration: Searches for recommended songs, creates a playlist, and adds songs to it.

Stripe Integration: Enables payment processing for premium features.

User-Friendly Interface: Simple input field for prompts with automated playlist creation.

Technologies Used

Backend: Java

Frontend: React TypeScript

APIs Used:

Spotify API

OpenAI API

Stripe API

Database: MongoDB

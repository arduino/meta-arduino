# Agent Instructions for meta-arduino

Project overview, layer architecture, and contribution guidelines live in
[README.md](README.md) and [DEVELOPERS.md](DEVELOPERS.md) — read those first.

## Commit style

- One topic per commit: never bundle multiple features, or unrelated
  bug fixes, into a single commit.
- Keep the commit message to a one-line subject (`<scope>: summary of the
  change`); skip the body unless a reviewer could not infer the change from
  the diff alone.
- Trailer order matters, so the sign-off is always the last line:

  ```
  <scope>: summary of the change

  Assisted-by: AGENT_NAME:MODEL_VERSION
  Signed-off-by: Author Name <author@example.com>
  ```

- Take `Signed-off-by` from the local git configuration (`git config
  user.name` / `user.email`); never fabricate a name or email.

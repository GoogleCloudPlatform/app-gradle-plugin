Want to contribute? Great! First, read this page (including the small print at the end).

## Before you contribute

Before we can use your code, you must sign the
[Google Individual Contributor License Agreement]
(https://cla.developers.google.com/about/google-individual)
(CLA), which you can do online. The CLA is necessary mainly because you own the
copyright to your changes, even after your contribution becomes part of our
codebase, so we need your permission to use and distribute your code. We also
need to be sure of various other things—for instance that you'll tell us if you
know that your code infringes on other people's patents. You don't have to sign
the CLA until after you've submitted your code for review and a member has
approved it, but you must do it before we can put your code into our codebase.
Before you start working on a larger contribution, you should get in touch with
us first through the issue tracker with your idea so that we can help out and
possibly guide you. Coordinating up front makes it much easier to avoid
frustration later on.

## Code reviews

All submissions, including submissions by project members, require review. We
use Github pull requests for this purpose.

Before submitting a pull request, please make sure to:

- Identify an existing [issue](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues) to associate with
  your proposed change, or [file a new issue](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/new).
- Describe any implementation plans in the issue and wait for a review from the repository maintainers.

### Typical Contribution Cycle

1. Set your git user.email property to the address used for signing the CLA. E.g.
   ```
   git config --global user.email "janedoe@google.com"
   ```
   If you're a Googler or other corporate contributor,
   use your corporate email address here, not your personal address.
2. Fork the repository into your own Github account.
3. Please include unit tests for all new code.
4. Check style and make sure all existing tests pass. (`./gradlew build`)
5. Associate the change with an existing issue or file a [new issue](../../issues)
6. Create a pull request!

## The small print

Contributions made by corporations are covered by a different agreement than
the one above, the
[Software Grant and Corporate Contributor License Agreement]
(https://cla.developers.google.com/about/google-corporate).
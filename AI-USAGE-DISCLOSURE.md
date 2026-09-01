# AI Usage Disclosure

## How AI was used

I used AI (Claude) to write the complete implementation for this project. My role
was to define the requirements, explain the problem as I understood it, and direct
the exploration — the AI generated the code.

For the assigned Transaction module, I gave AI the exact company requirements:
the four operations (create, get, update status, get customer transactions),
the required fields, and the mandatory test cases (successful creation, invalid
transaction rejected, duplicate ID rejected, non-existent lookup). I reviewed
the generated code and iterated with AI until it matched what was asked.

For the additional exploration, I described the scenarios myself and had AI
build them out, for example:

- "Users shouldn't have to manually type IDs — generate meaningful ones from
  entity data instead." → the ID Generation module.
- "If the Transaction module records transactions, what real-world ecosystem
  would generate those transactions?" → the Society/Anvita-inspired module
  (Resident, Society Admin, Property Manager, Security Staff, Merchant, Bills).
- "What connects the payer and receiver to the Transaction module?" → the
  Payment module with UPI/Card/Wallet/Net Banking strategies.

## How I validated it

Since I did not independently write the logic, I could not simply trust that
the AI-generated code was correct. I reviewed each module and worked with AI
to build a much larger test suite than the minimum required — 80 automated
tests in total — covering the required Transaction cases as well as the
service, controller, repository, validation, and status-transition logic
across every module. Running and passing this full suite was how I confirmed
the implementation actually behaved the way I intended, not just that it
compiled.

## What is mine vs. AI-generated

- Requirements, problem understanding, and the direction for every module:
  mine.
- Code implementation (entities, services, controllers, tests, exception
  handling): AI-generated, based on my instructions, and reviewed/verified by
  me through the test suite above.

## Elaboration: ID Generation — What I Changed, What AI Got Wrong, How I Verified It

### What happened
During the first iteration of generating unique Transaction IDs, AI's first
suggestion was to use a plain `UUID`. I felt this wasn't meaningful or
traceable back to real transaction data, so I synthesized my own approach —
building an ID from the actual entity fields (payer, receiver, type) instead
of a random value. AI did not adopt this direction cleanly; across roughly
4-5 iterated approaches, I noticed the suggestions were drifting and getting
less coherent — effectively hallucinating further away from a workable
design rather than converging on one.

### What I changed / corrected / rejected, and why
- Rejected the plain UUID approach — it satisfies uniqueness but carries no
  business meaning and isn't traceable to the transaction it belongs to.
- Rejected the following 4-5 AI-iterated variants in the main working
  session once I saw them getting less consistent rather than better.
- Instead, I moved the exploration into a **separate session**, iterated
  there until I had an approach I was satisfied with (build the ID from
  meaningful fields + a uniqueness suffix), and then brought only that
  final, chosen design back into the main project session to implement.

### What AI got wrong that I had to fix
Left unchecked, AI kept proposing variations rather than settling on one
sound design — several of the intermediate ideas would not have reliably
avoided collisions or would have leaned back on random values despite my
stated goal of meaningful, traceable IDs. I had to recognize this drift
myself and deliberately reset the process rather than accept whichever
version came out last.

### How I verified the final result actually works
The chosen design (`IdGenerator`) is covered by dedicated tests —
`TransactionIdGenerationTest`, `PaymentIdGenerationTest`,
`ActorIdGenerationTest`, and `IdGenerationInvalidInputTest` — which check
that generated IDs are non-null, correctly prefixed, and that invalid input
(no meaningful fields supplied) is rejected. I ran the full suite
(`./mvnw clean test`, 80/80 passing) to confirm the final approach behaves
correctly end-to-end, not just in isolation.
